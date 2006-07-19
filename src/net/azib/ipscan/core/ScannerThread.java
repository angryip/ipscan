/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.List;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.feeders.Feeder;

/**
 * Scanning thread.
 * 
 * @author anton
 */
public class ScannerThread extends Thread {

	private Scanner scanner;
	private Feeder feeder;
	private ScanningStateCallback statusCallback;
	private ScanningResultsCallback resultsCallback;
	private int state;
	private int runningThreads;
	private int maxThreads = Config.getGlobal().maxThreads;
	private int threadDelay = Config.getGlobal().threadDelay;
	
	public ScannerThread(Feeder feeder, List fetchers) {
		super("Scanner Thread");
		// this thread is daemon because we want JVM to terminate it
		// automatically if user closes the program (Main thread, that is)
		setDaemon(true);
		this.feeder = feeder;
		this.scanner = new Scanner(fetchers);
	}

	public void run() {
		changeStatus(ScanningStateCallback.STATE_SCANNING);
		while(feeder.hasNext() && state == ScanningStateCallback.STATE_SCANNING) {
			try {
				
				// make a small delay between thread creation
				Thread.sleep(threadDelay);
								
				if (runningThreads >= maxThreads) {
					// skip this iteration until more threads can be created
					continue;
				}
				
				// rerieve the next IP address to scan
				final InetAddress address = feeder.next();
				
				// check if this is a likely broadcast address and needs to be skipped
				if (Config.getGlobal().skipBroadcastAddresses && InetAddressUtils.isLikelyBroadcast(address)) {
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
		private int preparationNumber;
		
		public IPThread(InetAddress address, int preparationNumber) {
			super("IP Thread: " + address.getHostAddress());
			setDaemon(true);
			this.address = address;
			this.preparationNumber = preparationNumber;
		}

		public void run() {
			try {
				ScanningResult results = scanner.scan(address);
				resultsCallback.consumeResults(preparationNumber, results);
			}
			finally {
				runningThreads--;
			}
		}
	}
}
