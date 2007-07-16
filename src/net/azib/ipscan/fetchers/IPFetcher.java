/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

/**
 * Dummy fetcher, which is able to return the textual representation 
 * of the passed IP address.
 *
 * @author anton
 */
public class IPFetcher implements Fetcher {

	public static final String LABEL = "fetcher.ip";

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel()
	 */
	public String getLabel() {
		return LABEL;
	}

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		return subject.getAddress().getHostAddress();
	}

	public void init() {
	}

	public void cleanup() {
	}

}
