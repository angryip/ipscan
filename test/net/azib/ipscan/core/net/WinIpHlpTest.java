package net.azib.ipscan.core.net;

import org.junit.Test;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;
import static org.junit.Assert.assertArrayEquals;

public class WinIpHlpTest {
	@Test
	public void ipAddrHasBytesInNetworkOrderInMemory() throws Exception {
		var address = InetAddress.getByName("10.201.2.253");
		var memory = ByteBuffer.allocate(4).order(nativeOrder()).putInt(WinIpHlp.toIpAddr(address)).array();
		assertArrayEquals(address.getAddress(), memory);
	}
}
