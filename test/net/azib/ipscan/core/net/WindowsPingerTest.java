package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningSubject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class WindowsPingerTest extends AbstractPingerTest {
	public WindowsPingerTest() throws Exception {
		super(WindowsPinger.class);
	}

	@BeforeClass
	public static void beforeClass() {
		assumeTrue(Platform.WINDOWS);
	}

	@Test
	public void pingAliveIPv6() throws Exception {
		var localhost = InetAddress.getByName("::1");
		assumeTrue(NetworkInterface.getByInetAddress(localhost) != null);

		var result = pinger.ping(new ScanningSubject(localhost), 2);
		assertTrue(result.isAlive());
		assertEquals(2, result.getPacketCount());
		assertEquals(2, result.getReplyCount());
	}
}
