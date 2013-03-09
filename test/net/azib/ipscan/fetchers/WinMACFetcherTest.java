package net.azib.ipscan.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WinMACFetcherTest {
	@Test
	public void bytesToMAC() throws Exception {
		assertEquals("", WinMACFetcher.bytesToMAC(new byte[0]));
		assertEquals("00:01:02:0D", WinMACFetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
	}
}
