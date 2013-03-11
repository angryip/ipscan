package net.azib.ipscan.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MACFetcherTest {
	@Test
	public void extractMAC() throws Exception {
		assertEquals("E4:48:C7:EE:28:C2", UnixMACFetcher.extractMAC("? (192.168.0.1) at e4:48:c7:ee:28:c2 [ether] on wlan0"));
	}

	@Test
	public void bytesToMAC() throws Exception {
		assertEquals("", WinMACFetcher.bytesToMAC(new byte[0]));
		assertEquals("00:01:02:0D", WinMACFetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
	}
}
