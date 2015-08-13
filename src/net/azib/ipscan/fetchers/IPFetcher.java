/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.InetAddressHolder;

import javax.inject.Inject;

/**
 * Dummy fetcher, which is able to return the textual representation 
 * of the passed IP address.
 *
 * @author Anton Keks
 */
public class IPFetcher extends AbstractFetcher {
	@Inject public IPFetcher() {}

	public static final String ID = "fetcher.ip";

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		return new InetAddressHolder(subject.getAddress());
	}

}
