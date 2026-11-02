package net.azib.ipscan.core.net;

import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class PingResultTest {
	@Test
	public void mergeAddsTimes() {
		var address = InetAddress.getLoopbackAddress();
		var first = new PingResult(address, 2);
		first.addReply(10);
		first.addReply(30);
		first.setTTL(64);

		var second = new PingResult(address, 2);
		second.addReply(20);
		second.addReply(40);

		first.merge(second);

		assertEquals(4, first.getPacketCount());
		assertEquals(4, first.getReplyCount());
		assertEquals(25, first.getAverageTime());
		assertEquals(40, first.getLongestTime());
		assertEquals(64, first.getTTL());
	}
}
