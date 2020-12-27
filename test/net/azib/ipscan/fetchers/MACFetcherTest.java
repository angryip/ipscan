package net.azib.ipscan.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MACFetcherTest {
	private MACFetcher fetcher = new UnixMACFetcher();

	@Test
	public void extractMAC() throws Exception {
		assertEquals("E4:48:C7:EE:28:C2", fetcher.extractMAC("? (192.168.0.1) at e4:48:c7:ee:28:c2 [ether] on wlan0"));
	}

	@Test
	public void extractMACAddsLeadingZeroesOnOsX() throws Exception {
		assertEquals("04:48:07:EE:28:02", fetcher.extractMAC("? (192.168.0.1) at 4:48:7:ee:28:2 [ether] on wlan0"));
		assertEquals("C4:2C:03:08:1E:89", fetcher.extractMAC("? (10.10.10.96) at c4:2c:3:8:1e:89 on en0 ifscope permanent [ethernet]"));
	}

	@Test
	public void bytesToMAC() throws Exception {
		assertEquals("", fetcher.bytesToMAC(new byte[0]));
		assertEquals("00:01:02:0D", fetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
	}

	@Test
	public void redefinedSeparator() {
		fetcher.separator = "-";
		assertEquals("00-01-02-0D", fetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
		assertEquals("04-48-07-EE-28-02", fetcher.extractMAC("? (192.168.0.1) at 4:48:7:ee:28:2 [ether] on wlan0"));
	}
}
