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
	
	public ScannerThread(Feeder feeder, Scanner scanner, StateMachine stateMachine, ScanningProgressCallback progressCallback, ScanningResultList scanningResults, GlobalConfig globalConfig) {
		super("Scanner Thread");
		this.config = globalConfig;
		this.stateMachine = stateMachine;
		this.progressCallback = progressCallback;
		
		// this thread is daemon because we want JVM to terminate it
		// automatically if user closes the program (Main thread, that is)
		setDaemon(true);
		this.feeder = feeder;
		this.scanner = scanner;
		this.scanningResultList = scanningResults;
		
		// initialize in the main thread in order to catch exceptions gracefully
		scanner.init();
	}

	public void run() {
		stateMachine.transitionTo(ScanningState.SCANNING);
				
		while(feeder.hasNext() && stateMachine.isState(ScanningState.SCANNING)) {
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
				int preparationNumber = resultsCallback.prepareForResults(address);
				
				// notify listeners of the progress we are doing
				progressCallback.updateProgress(address, runningThreads, feeder.getPercentageComplete());
				
				// scan each IP in parallel, in a separate thread
				new IPThread(address, preparationNumber).start();
			}
			catch (InterruptedException e) {				
				return;
			}
		}
		
		// inform that no more addresses left
		stateMachine.transitionTo(ScanningState.STOPPING);

		// now wait for all threads, which are still running
		try {
			// TODO: make a better and safer implementation
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
		stateMachine.transitionTo(ScanningState.IDLE);
	}
		
	public void forceStop() {
		stateMachine.transitionTo(ScanningState.STOPPING);
	}
	
	public void abort() {
		stateMachine.transitionTo(ScanningState.KILLING);
	}
	
	// TODO: remove me and change to constructor injection
	public void setResultsCallback(ScanningResultsCallback resultsCallback) {
		this.resultsCallback = resultsCallback;
	}
	
	/**
	 * This thread gets executed for each scanned IP address to do the actual
	 * scanning.
	 */
	private class IPThread extends Thread {
		private InetAddress address;
		private int preparationIndex;
		
		IPThread(InetAddress address, int preparationIndex) {
			super("IP Thread: " + address.getHostAddress());
			setDaemon(true);
			this.address = address;
			this.preparationIndex = preparationIndex;
		}

		public void run() {
			try {
				ScanningResult result = scanningResultList.getResult(preparationIndex); 
				scanner.scan(address, result);
				resultsCallback.consumeResults(preparationIndex, result);
			}
			finally {
				runningThreads--;
			}
		}
	}
}
