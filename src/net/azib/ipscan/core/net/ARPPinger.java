package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.MACFetcher;

import java.io.IOException;

import static java.lang.System.currentTimeMillis;

public class ARPPinger implements Pinger {
	public static final String ID = "pinger.arp";
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

	@Override public String getId() {
		return ID;
	}

	@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
		if (trigger != null) count -= count / 2;
		var result = new PingResult(subject.getAddress(), count);
		for (var i = 0; i < count; i++) {
			var start = currentTimeMillis();
			if (trigger != null) {
				// this should issue an ARP request for the IP
				result.merge(trigger.ping(subject, 1));
			}
			var mac = macFetcher.scan(subject);
			if (mac != null) result.addReply(currentTimeMillis() - start);
		}
		return result;
	}
}
