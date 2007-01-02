/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.feeders.Feeder;

/**
 * Scanning thread.
 * 
 * @author anton
 */
public class ScannerThread extends Thread {

	private Scanner scanner;
	private ScanningResultList scanningResultList;
	private Feeder feeder;
	private ScanningStateCallback statusCallback;
	private ScanningResultsCallback resultsCallback;
	private int state;
	private int runningThreads;
	
	private GlobalConfig config;
	
	public ScannerThread(Feeder feeder, Scanner scanner, ScanningResultList scanningResults, GlobalConfig globalConfig) {
		super("Scanner Thread");
		this.config = globalConfig;
		
		// this thread is daemon because we want JVM to terminate it
		// automatically if user closes the program (Main thread, that is)
		setDaemon(true);
		this.feeder = feeder;
		this.scanner = scanner;
		this.scanningResultList = scanningResults;
	}

	public void run() {
		changeStatus(ScanningStateCallback.STATE_SCANNING);
		
		scanner.init();
		
		while(feeder.hasNext() && state == ScanningStateCallback.STATE_SCANNING) {
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
				statusCallback.updateProgress(address, runningThreads, feeder.getPercentageComplete());
				
				// scan each IP in parallel, in a separate thread
				new IPThread(address, preparationNumber).start();
			}
			catch (InterruptedException e) {				
				return;
			}
		}
		
		// inform that no more addresses left
		changeStatus(ScanningStateCallback.STATE_STOPPING);

		// now wait for all threads, which are still running
		try {
			// TODO: make a better and safer implementation
			while (runningThreads > 0) {
				Thread.sleep(200);
				statusCallback.updateProgress(null, runningThreads, 100);
			}
		} 
		catch (InterruptedException e) {
			// nothing special to do here
		}
		
		scanner.cleanup();
		
		// finally, the scanning is complete
		changeStatus(ScanningStateCallback.STATE_IDLE);
	}
	
	private void changeStatus(int status) {
		this.state = status;
		statusCallback.scannerStateChanged(status);
	}
	
	public void forceStop() {
		changeStatus(ScanningStateCallback.STATE_STOPPING);
	}
	
	public void abort() {
		changeStatus(ScanningStateCallback.STATE_KILLING);
	}

	public void setResultsCallback(ScanningResultsCallback resultsCallback) {
		this.resultsCallback = resultsCallback;
	}
	
	public void setStatusCallback(ScanningStateCallback statusCallback) {
		this.statusCallback = statusCallback;
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
