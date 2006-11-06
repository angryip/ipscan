/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.Pinger;

/**
 * PingTTLFetcher shares pinging results with PingFetcher
 * and returns the TTL field of the last received packet.
 *
 * @author anton
 */
public class PingTTLFetcher extends PingFetcher {
	
	public String getLabel() {
		return "fetcher.ping.ttl";
	}

	public Object scan(ScanningSubject subject) {
		Pinger pinger = executePing(subject);
		boolean isAlive = pinger != null && !pinger.isTimeout();
		subject.setResultType(isAlive ? ScanningSubject.RESULT_TYPE_ALIVE : ScanningSubject.RESULT_TYPE_DEAD);
		return isAlive ? new Integer(pinger.getTTL()) : null;
	}
}
