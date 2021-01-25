package net.azib.ipscan.fetchers;

import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class MACVendorFetcherTest {
	@Test
	public void findMACVendor() {
		MACFetcher macFetcher = new MACFetcher() {
			@Override protected String resolveMAC(InetAddress address) { return null; }
		};
		MACVendorFetcher fetcher = new MACVendorFetcher(macFetcher);
		fetcher.init();
		assertEquals("XEROX", fetcher.findMACVendor("00:00:01:00:00:00"));
		assertEquals("Nokia", fetcher.findMACVendor("FC:E5:57:11:22:33"));

		macFetcher.separator = "-";
		assertEquals("Nokia", fetcher.findMACVendor("FC-E5-57-11-22-33"));
	}
}
