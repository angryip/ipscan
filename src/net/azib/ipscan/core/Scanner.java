/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.PingFetcher;

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
		boolean continueScanning = true;
		int fetcherIndex = 0;
		for (Iterator i = fetcherRegistry.getRegisteredFetchers().iterator(); i.hasNext();) {
			Fetcher fetcher = (Fetcher) i.next();
			if (continueScanning) {
				String value = fetcher.scan(scanningSubject);
				// TODO: write better code
				if (!Config.getGlobal().scanDeadHosts && fetcher instanceof PingFetcher) {
					continueScanning = value != null;
					// TODO: hardcoded [timeout]
					//value = value == null ? "[timeout]" : value;
				}
				result.setValue(fetcherIndex, value);
			}
			// TODO: display something in the else
			fetcherIndex++;
		}
		
		result.setType(scanningSubject.getResultType());
	}
	
}
