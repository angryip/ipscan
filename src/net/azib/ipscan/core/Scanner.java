/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.MACFetcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scanner functionality is encapsulated in this class.
 * It uses a list of fetchers to perform the actual scanning.
 * 
 * @author Anton Keks
 */
public class Scanner {
	private static final Logger LOG = Logger.getLogger(Scanner.class.getName());
	private FetcherRegistry fetcherRegistry;
	private Map<Long, Fetcher> activeFetchers = new ConcurrentHashMap<>();

	public Scanner(FetcherRegistry fetcherRegistry) {
		this.fetcherRegistry = fetcherRegistry;
	}

	/**
	 * Executes all registered fetchers for the current IP address.
	 * @param subject containing the IP address to scan
	 * @param result where the results are injected
	 */
	public void scan(ScanningSubject subject, ScanningResult result) {
		int fetcherIndex = 0;
		boolean isScanningInterrupted = false;
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			Object value = NotScanned.VALUE;
			try {
				activeFetchers.put(Thread.currentThread().getId(), fetcher);
				if (!subject.isAddressAborted() && !isScanningInterrupted) {
					// run the fetcher
					value = fetcher.scan(subject);
					// check if scanning was interrupted
					isScanningInterrupted = Thread.currentThread().isInterrupted();
					if (value == null)
						value = isScanningInterrupted ? NotScanned.VALUE : NotAvailable.VALUE;
				}
			}
			catch (Throwable e) {
				LOG.log(Level.SEVERE, "", e);
			}
			// store the value
			result.setValue(fetcherIndex, value);
			fetcherIndex++;
		}
		result.setMac((String) subject.getParameter(MACFetcher.ID));
		activeFetchers.remove(Thread.currentThread().getId());
		
		result.setType(subject.getResultType());
	}

	public void interrupt(Thread thread) {
		Fetcher fetcher = activeFetchers.get(thread.getId());
		if (fetcher != null) fetcher.cleanup();
	}
	
	/**
	 * Init everything needed for scanning, including Fetchers
	 */
	public void init(Feeder feeder) {
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			fetcher.init(feeder);
		}
	}
	
	/**
	 * Cleanup after a scan
	 */
	public void cleanup() {
		activeFetchers.clear();
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			fetcher.cleanup();
		}
	}
}
