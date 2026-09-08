package net.azib.ipscan.core.net;

import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import static org.junit.Assert.*;

abstract class AbstractPingerTest {
	Pinger pinger;

	AbstractPingerTest(Class<? extends Pinger> pingerClass) throws Exception {
		var injector = new ComponentRegistry().init(false);
		this.pinger = injector.require(pingerClass);
	}

	@Test
	public void pingAlive() throws IOException {
		var ifAddr = InetAddressUtils.getLocalInterface();
		// open a local listening port so that port-based pingers (e.g. TCP) always have
		// a guaranteed open port - avoids false negatives caused by firewalls silently
		// dropping probes of closed ports; other pinger types simply ignore it
		try (var server = new ServerSocket(0)) {
			var subject = new ScanningSubject(ifAddr.getAddress());
			subject.addRequestedPort(server.getLocalPort());
			var result = pinger.ping(subject, 2);
			assertTrue(result.isAlive());
			assertEquals(2, result.getPacketCount());
			assertEquals(2, result.getReplyCount());
			assertTrue(result.getAverageTime() <= 10);
			assertTrue(result.getTTL() >= 0);
		}
	}

	@Test
	public void pingDead() throws IOException {
		var result = pinger.ping(new ScanningSubject(InetAddress.getByName("192.168.99.253")), 1);
		assertFalse(result.isAlive());
	}
}
