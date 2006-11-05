/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Iterator;

import net.azib.ipscan.config.Labels;
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
				String value = fetcher.scan(scanningSubject);
				if (value == null) 
					value = Labels.getLabel("fetcher.value.nothing");
				result.setValue(fetcherIndex, value);
			}
			else {
				result.setValue(fetcherIndex, Labels.getLabel("fetcher.value.aborted"));
			}
			fetcherIndex++;
		}
		
		result.setType(scanningSubject.getResultType());
	}
	
}
