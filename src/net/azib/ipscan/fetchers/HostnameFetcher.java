/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

/**
 * HostnameFetcher retrieves hostnames of IP addresses by reverse DNS lookups.
 * 
 * @author anton
 */
public class HostnameFetcher implements Fetcher {

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel()
	 */
	public String getLabel() {
		return "fetcher.hostname";
	}

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		String hostname = subject.getIPAddress().getCanonicalHostName();
		// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
		return subject.getIPAddress().getHostAddress().equals(hostname) ? null : hostname;
	}

}
