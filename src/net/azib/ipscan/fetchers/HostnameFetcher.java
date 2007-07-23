/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

/**
 * HostnameFetcher retrieves hostnames of IP addresses by reverse DNS lookups.
 * 
 * @author Anton Keks
 */
public class HostnameFetcher implements Fetcher {

	static final String LABEL = "fetcher.hostname";

	/*
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel()
	 */
	public String getLabel() {
		return LABEL;
	}

	/*
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		String hostname = subject.getAddress().getCanonicalHostName();
		// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
		return subject.getAddress().getHostAddress().equals(hostname) ? null : hostname;
	}

	public void init() {
	}

	public void cleanup() {
	}

}
