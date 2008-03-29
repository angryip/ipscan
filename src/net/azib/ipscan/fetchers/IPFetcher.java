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
public class IPFetcher extends AbstractFetcher {

	public static final String ID = "fetcher.ip";

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		return new InetAddressValue(subject.getAddress());
	}

}
