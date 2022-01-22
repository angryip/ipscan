package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.MACFetcher;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class ARPPinger implements Pinger {
	private MACFetcher macFetcher;
	private Pinger trigger;

	public ARPPinger(MACFetcher macFetcher, JavaPinger trigger) {
		// WinMACFetcher sends an actual ARP request, so no previous UDP request is needed
		this(macFetcher, macFetcher.getClass().getSimpleName().startsWith("Win") ? null : (Pinger) trigger);
	}

	ARPPinger(MACFetcher macFetcher, Pinger trigger) {
		this.macFetcher = macFetcher;
		this.trigger = trigger;
	}

	@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress(), count);
		if (trigger != null) count /= 2;
		for (int i = 0; i < count; i++) {
			long start = currentTimeMillis();
			if (trigger != null) {
				// this should issue an ARP request for the IP
				result.merge(trigger.ping(subject, count / 2));
			}
			String mac = macFetcher.scan(subject);
			if (mac != null) result.addReply(currentTimeMillis() - start);
		}
		return result;
	}
}
