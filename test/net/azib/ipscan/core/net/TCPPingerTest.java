package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TCPPingerTest extends AbstractPingerTest {
	public TCPPingerTest() throws Exception {
		super(TCPPinger.class);
	}

	@Test
	@Override
	public void pingAlive() throws IOException {
		var ifAddr = InetAddressUtils.getLocalInterface();
		try (var server = new ServerSocket(0, 50, ifAddr.getAddress())) {
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
}
