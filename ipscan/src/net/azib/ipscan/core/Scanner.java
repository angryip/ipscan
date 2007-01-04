/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Iterator;

import net.azib.ipscan.core.values.NotAvailableValue;
import net.azib.ipscan.core.values.NotScannedValue;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * Scanner functionality is encapsulated in this class.
 * It uses a list of fetchers to perform the actual scanning.
 * 
 * @author anton
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
		for (Iterator i = fetcherRegistry.getSelectedFetchers().iterator(); i.hasNext();) {
			Fetcher fetcher = (Fetcher) i.next();
			if (!scanningSubject.isScanningAborted()) {
				Object value = fetcher.scan(scanningSubject);
				result.setValue(fetcherIndex, value != null ? value : NotAvailableValue.INSTANCE);
			}
			else {
				result.setValue(fetcherIndex, NotScannedValue.INSTANCE);
			}
			fetcherIndex++;
		}
		
		result.setType(scanningSubject.getResultType());
	}
	
	/**
	 * Init everything needed for scanning, including Fetchers
	 */
	public void init() {
		for (Iterator i = fetcherRegistry.getSelectedFetchers().iterator(); i.hasNext();) {
			((Fetcher)i.next()).init();
		}
	}
	
	/**
	 * Cleanup after a scan
	 */
	public void cleanup() {
		for (Iterator i = fetcherRegistry.getSelectedFetchers().iterator(); i.hasNext();) {
			((Fetcher)i.next()).cleanup();
		}
	}
	
}
