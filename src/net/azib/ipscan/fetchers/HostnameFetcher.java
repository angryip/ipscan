/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

/**
 * HostnameFetcher retrieves hostnames of IP addresses by reverse DNS lookups.
 * 
 * @author Anton Keks
 */
public class HostnameFetcher extends AbstractFetcher {

	public static final String ID = "fetcher.hostname";

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		String hostname = subject.getAddress().getCanonicalHostName();
		// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
		return subject.getAddress().getHostAddress().equals(hostname) ? null : hostname;
	}

}
