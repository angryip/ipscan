package net.azib.ipscan.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MACVendorFetcherTest {
	@Test
	public void findMACVendor() throws Exception {
		MACVendorFetcher fetcher = new MACVendorFetcher(null);
		fetcher.init();
		assertEquals("XeroxCor", fetcher.findMACVendor("00:00:01:00:00:00"));
		assertEquals("NokiaCor", fetcher.findMACVendor("FC:E5:57:11:22:33"));
	}
}
