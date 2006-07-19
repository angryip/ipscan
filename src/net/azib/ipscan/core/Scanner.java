/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.PingFetcher;

/**
 * Scanner functionality is encapsulated in this class.
 * It uses a list of fetchers to perform the actual scanning.
 * 
 * @author anton
 */
public class Scanner {
	
	/** The List of Fetchers, which are used for this scan */ 
	private List fetchers;
	
	public Scanner(List fetchers) {
		this.fetchers = fetchers;
	}

	/**
	 * Executes all registered fetchers for the current IP address.
	 * @return results
	 */
	public ScanningResult scan(InetAddress address) {
		
		// create a scanning subject object, which will be used by fetchers
		// to cache common information
		ScanningSubject scanningSubject = new ScanningSubject(address);
		
		// reset results
		List values = new ArrayList();
		
		// populate results
		boolean continueScanning = true;
		for (Iterator i = fetchers.iterator(); i.hasNext();) {
			Fetcher fetcher = (Fetcher) i.next();
			if (continueScanning) {
				String result = fetcher.scan(scanningSubject);
				// TODO: write better code
				if (!Config.getGlobal().scanDeadHosts && fetcher instanceof PingFetcher) {
					continueScanning = result != null;
					// TODO: hardcoded [timeout]
					//result = result == null ? "[timeout]" : result;
				}
				values.add(result);
			}
			// TODO: display something in the else
		}
		
		return new ScanningResult(values, scanningSubject.getResultType());
	}
	
}
