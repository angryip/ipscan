/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.feeders.Feeder;

/**
 * Scanning thread.
 * 
 * @author anton
 */
public class ScannerThread extends Thread {

	private Scanner scanner;
	private StateMachine stateMachine;
	private ScanningResultList scanningResultList;
	private Feeder feeder;
	private ScanningProgressCallback progressCallback;
	private ScanningResultsCallback resultsCallback;
	private int runningThreads;
	
	private GlobalConfig config;
	
	public ScannerThread(Feeder feeder, Scanner scanner, StateMachine stateMachine, ScanningProgressCallback progressCallback, ScanningResultList scanningResults, GlobalConfig globalConfig, ScanningResultsCallback resultsCallback) {
		super("Scanner Thread");
		this.config = globalConfig;
		this.stateMachine = stateMachine;
		this.progressCallback = progressCallback;
		this.resultsCallback = resultsCallback;
		
		// this thread is daemon because we want JVM to terminate it
		// automatically if user closes the program (Main thread, that is)
		setDaemon(true);
		
		this.feeder = feeder;
		this.scanner = scanner;
		this.scanningResultList = scanningResults;
		this.scanningResultList.initNewScan(feeder);
		
		// initialize in the main thread in order to catch exceptions gracefully
		scanner.init();
	}

	public void run() {
		while(feeder.hasNext() && stateMachine.inState(ScanningState.SCANNING)) {
			try {
				// make a small delay between thread creation
				Thread.sleep(config.threadDelay);
								
				if (runningThreads >= config.maxThreads) {
					// skip this iteration until more threads can be created
					continue;
				}
				
				// rerieve the next IP address to scan
				final InetAddress address = feeder.next();
				
				// check if this is a likely broadcast address and needs to be skipped
				if (config.skipBroadcastAddresses && InetAddressUtils.isLikelyBroadcast(address)) {
					continue;
				}

				// now increment the number of active threads, because we are going
				// to start a new one below
				runningThreads++;
								
				// prepare results receiver for upcoming results
				ScanningResult result = scanningResultList.createResult(address);
				resultsCallback.prepareForResults(result);
				
				// notify listeners of the progress we are doing
				progressCallback.updateProgress(address, runningThreads, feeder.percentageComplete());
				
				// scan each IP in parallel, in a separate thread
				new IPThread(address, result).start();
			}
			catch (InterruptedException e) {
				return;
			}
		}
		
		// inform that no more addresses left
		stateMachine.stop();
		
		// now wait for all threads, which are still running
		try {
			// TODO: make a better and safer implementation (synchronization?)
			while (runningThreads > 0) {
				Thread.sleep(200);
				progressCallback.updateProgress(null, runningThreads, 100);
			}
		} 
		catch (InterruptedException e) {
			// nothing special to do here
		}
		
		scanner.cleanup();
		
		// finally, the scanning is complete
		scanningResultList.setScanningFinished(true);
		stateMachine.complete();		
	}
				
	/**
	 * This thread gets executed for each scanned IP address to do the actual
	 * scanning.
	 */
	private class IPThread extends Thread {
		private InetAddress address;
		private ScanningResult result;
		
		IPThread(InetAddress address, ScanningResult result) {
			super("IP Thread: " + address.getHostAddress());
			setDaemon(true);
			this.address = address;
			this.result = result;
		}

		public void run() {
			try {
				scanner.scan(address, result);
				resultsCallback.consumeResults(result);
			}
			finally {
				runningThreads--;
			}
		}
	}
}
