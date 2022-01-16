package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.MACFetcher;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class ARPPinger implements Pinger {
	private MACFetcher macFetcher;
	private JavaPinger trigger;

	public ARPPinger(MACFetcher macFetcher, JavaPinger trigger) {
		this.macFetcher = macFetcher;
		// WinMACFetcher sends an actual ARP request, so no previous UDP request is needed
		this.trigger = macFetcher.getClass().getSimpleName().startsWith("Win") ? null : trigger;
	}

	@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress(), 1);
		for (int i = 0; i < count; i++) {
			long start = currentTimeMillis();
			if (trigger != null) trigger.ping(subject, 1); // this should issue an ARP request for the IP
			String mac = macFetcher.scan(subject);
			if (mac != null) result.addReply(currentTimeMillis() - start);
		}
		return result;
	}
}
