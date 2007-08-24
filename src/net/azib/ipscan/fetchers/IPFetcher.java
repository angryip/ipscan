/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.InetAddressValue;

/**
 * Dummy fetcher, which is able to return the textual representation 
 * of the passed IP address.
 *
 * @author Anton Keks
 */
public class IPFetcher implements Fetcher {

	public static final String LABEL = "fetcher.ip";

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel()
	 */
	public String getLabel() {
		return LABEL;
	}

	public Object scan(ScanningSubject subject) {
		return new InetAddressValue(subject.getAddress());
	}

	public void init() {
	}

	public void cleanup() {
	}

}
