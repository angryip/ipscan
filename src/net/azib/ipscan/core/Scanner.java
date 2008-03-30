/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

import net.azib.ipscan.core.values.NotAvailableValue;
import net.azib.ipscan.core.values.NotScannedValue;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * Scanner functionality is encapsulated in this class.
 * It uses a list of fetchers to perform the actual scanning.
 * 
 * @author Anton Keks
 */
public class Scanner {
	
	private FetcherRegistry fetcherRegistry;
	
	public Scanner(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
	}

	/**
	 * Executes all registered fetchers for the current IP address.
	 * @param address the IP address to scan
	 * @param result where the results are injected
	 */
	public void scan(InetAddress address, ScanningResult result) {
		
		// create a scanning subject object, which will be used by fetchers
		// to cache common information
		ScanningSubject scanningSubject = new ScanningSubject(address);
		
		// populate results
		int fetcherIndex = 0;
		boolean isScanningInterrupted = false;
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			Object value = NotScannedValue.INSTANCE;
			if (!scanningSubject.isAddressScanningAborted() && !isScanningInterrupted) {
				// run the fetcher
				value = fetcher.scan(scanningSubject);
				// check if scanning was interrupted
				isScanningInterrupted = Thread.currentThread().isInterrupted();
				if (value == null)
					value = isScanningInterrupted ? NotScannedValue.INSTANCE : NotAvailableValue.INSTANCE;
			}
			// store the value
			result.setValue(fetcherIndex, value);
			fetcherIndex++;
		}
		
		result.setType(scanningSubject.getResultType());
	}
	
	/**
	 * Init everything needed for scanning, including Fetchers
	 */
	public void init() {
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			fetcher.init();
		}
	}
	
	/**
	 * Cleanup after a scan
	 */
	public void cleanup() {
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			fetcher.cleanup();
		}
	}
	
}
