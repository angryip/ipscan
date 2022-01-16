package net.azib.ipscan.core.net;

import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.util.InetAddressUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

abstract class AbstractPingerTest {
	Pinger pinger;

	AbstractPingerTest(Class<? extends Pinger> pingerClass) throws Exception {
		Injector injector = new ComponentRegistry().init(false);
		this.pinger = injector.require(pingerClass);
	}

	@Test
	public void pingAlive() throws IOException {
		InterfaceAddress ifAddr = InetAddressUtils.getLocalInterface();
		PingResult result = pinger.ping(new ScanningSubject(ifAddr.getAddress()), 2);
		assertTrue(result.isAlive());
		assertTrue(result.getAverageTime() <= 10);
		assertTrue(result.getTTL() >= 0);
	}

	@Test
	public void pingDead() throws IOException {
		PingResult result = pinger.ping(new ScanningSubject(InetAddress.getByName("192.168.99.253")), 1);
		assertFalse(result.isAlive());
	}
}
