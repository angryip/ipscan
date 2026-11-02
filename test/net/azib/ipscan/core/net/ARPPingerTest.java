package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.MACFetcher;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class ARPPingerTest extends AbstractPingerTest {
	public ARPPingerTest() throws Exception {
		super(ARPPinger.class);
	}

	@Test @Override
	public void pingAlive() throws IOException {
		assumeTrue(Platform.LINUX);
		super.pingAlive();
	}

	@Test
	public void usesTriggerResultWhenAlive() throws IOException {
		var macFetcher = new FakeMACFetcher();
		var triggerResult = new PingResult(InetAddress.getLoopbackAddress(), 3);
		triggerResult.addReply(42);
		var pinger = new ARPPinger(macFetcher, new FakePinger(triggerResult));

		var result = pinger.ping(new ScanningSubject(InetAddress.getLoopbackAddress()), 3);

		assertSame(triggerResult, result);
		assertEquals(0, macFetcher.calls);
	}

	@Test
	public void fallsBackToArpWhenTriggerIsDead() throws IOException {
		var macFetcher = new FakeMACFetcher();
		macFetcher.mac = "AA:BB:CC:DD:EE:FF";
		var triggerResult = new PingResult(InetAddress.getLoopbackAddress(), 3);
		var pinger = new ARPPinger(macFetcher, new FakePinger(triggerResult));

		var result = pinger.ping(new ScanningSubject(InetAddress.getLoopbackAddress()), 3);

		assertTrue(result.isAlive());
		assertEquals(3, result.getPacketCount());
		assertEquals(3, result.getReplyCount());
	}

	static class FakeMACFetcher extends MACFetcher {
		int calls;
		String mac;

		@Override protected String resolveMAC(ScanningSubject subject) {
			calls++;
			return mac;
		}
	}

	static class FakePinger implements Pinger {
		final PingResult result;

		FakePinger(PingResult result) {
			this.result = result;
		}

		@Override public String getId() {
			return "fake";
		}

		@Override public PingResult ping(ScanningSubject subject, int count) {
			return result;
		}
	}
}
