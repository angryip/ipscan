/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.PingerRegistry;

/**
 * PingTTLFetcher shares pinging results with PingFetcher
 * and returns the TTL field of the last received packet.
 *
 * @author anton
 */
public class PingTTLFetcher extends PingFetcher {
	
	public PingTTLFetcher(PingerRegistry pingerRegistry, GlobalConfig globalConfig) {
		super(pingerRegistry, globalConfig);
	}

	public String getLabel() {
		return "fetcher.ping.ttl";
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ScanningSubject.RESULT_TYPE_ALIVE : ScanningSubject.RESULT_TYPE_DEAD);
		return result.isAlive() && result.getTTL() > 0 ? new Integer(result.getTTL()) : null;
	}
}
