/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.PingerRegistry;

import javax.inject.Inject;

/**
 * PingTTLFetcher shares pinging results with PingFetcher
 * and returns the TTL field of the last received packet.
 *
 * @author Anton Keks
 */
public class PingTTLFetcher extends PingFetcher {
	
	@Inject public PingTTLFetcher(PingerRegistry pingerRegistry, ScannerConfig scannerConfig) {
		super(pingerRegistry, scannerConfig);
	}

	public String getId() {
		return "fetcher.ping.ttl";
	}

	public Object scan(ScanningSubject subject) {
		PingResult result = executePing(subject);
		subject.setResultType(result.isAlive() ? ResultType.ALIVE : ResultType.DEAD);
		return result.isAlive() && result.getTTL() > 0 ? result.getTTL() : null;
	}
}
