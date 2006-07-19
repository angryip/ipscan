/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

/**
 * HostnameFetcher retrieves hostnames of IP addresses
 * by reverse DNS lookups.
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
	public String scan(ScanningSubject subject) {
		String hostname = subject.getIPAddress().getCanonicalHostName();
		// TODO: in case hostname is not found, this method will return the 
		// textual IP address, which is probably not what we want here...
		return hostname;
	}

}
