package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MACVendorFetcherTest {
	@Test
	public void findMACVendor() {
		var macFetcher = new MACFetcher() {
			@Override protected String resolveMAC(ScanningSubject subject) { return null; }
		};
		var fetcher = new MACVendorFetcher(macFetcher);
		fetcher.init();
		assertEquals("XEROX", fetcher.findMACVendor("00:00:01:00:00:00"));
		assertEquals("Nokia", fetcher.findMACVendor("FC:E5:57:11:22:33"));
	}
}
