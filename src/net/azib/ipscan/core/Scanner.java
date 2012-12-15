/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NotScanned;
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
	 * @param subject containing the IP address to scan
	 * @param result where the results are injected
	 */
	public void scan(ScanningSubject subject, ScanningResult result) {
		// populate results
		int fetcherIndex = 0;
		boolean isScanningInterrupted = false;
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			Object value = NotScanned.VALUE;
			if (!subject.isAddressAborted() && !isScanningInterrupted) {
				// run the fetcher
				value = fetcher.scan(subject);
				// check if scanning was interrupted
				isScanningInterrupted = Thread.currentThread().isInterrupted();
				if (value == null)
					value = isScanningInterrupted ? NotScanned.VALUE : NotAvailable.VALUE;
			}
			// store the value
			result.setValue(fetcherIndex, value);
			fetcherIndex++;
		}
		
		result.setType(subject.getResultType());
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
