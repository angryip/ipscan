package net.azib.ipscan.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MACFetcherTest {
	@Test
	public void extractMAC() throws Exception {
		assertEquals("E4:48:C7:EE:28:C2", UnixMACFetcher.extractMAC("? (192.168.0.1) at e4:48:c7:ee:28:c2 [ether] on wlan0"));
	}

	@Test
	public void extractMACAddsLeadingZeroesOnOsX() throws Exception {
		assertEquals("04:48:07:EE:28:02", UnixMACFetcher.extractMAC("? (192.168.0.1) at 4:48:7:ee:28:2 [ether] on wlan0"));
		assertEquals("C4:2C:03:08:1E:89", UnixMACFetcher.extractMAC("? (10.10.10.96) at c4:2c:3:8:1e:89 on en0 ifscope permanent [ethernet]"));
	}

	@Test
	public void bytesToMAC() throws Exception {
		assertEquals("", WinMACFetcher.bytesToMAC(new byte[0]));
		assertEquals("00:01:02:0D", WinMACFetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
	}
}
