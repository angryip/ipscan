/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Iterator;

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
	
}
