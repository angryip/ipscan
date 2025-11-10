package net.azib.ipscan.core.net;

import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

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
		var result = pinger.ping(new ScanningSubject(ifAddr.getAddress()), 2);
		assertTrue(result.isAlive());
		assertEquals(2, result.getPacketCount());
		assertEquals(2, result.getReplyCount());
		assertTrue(result.getAverageTime() <= 10);
		assertTrue(result.getTTL() >= 0);
	}

	@Test
	public void pingDead() throws IOException {
		var result = pinger.ping(new ScanningSubject(InetAddress.getByName("192.168.99.253")), 1);
		assertFalse(result.isAlive());
	}
}
