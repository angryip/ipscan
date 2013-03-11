package net.azib.ipscan.fetchers;

import net.azib.ipscan.fetchers.UnixMACFetcher;
import org.junit.Test;
import static org.junit.Assert.*;

public class UnixMACFetcherTest {
	@Test
	public void extractMAC() throws Exception {
		assertEquals("E4:48:C7:EE:28:C2", UnixMACFetcher.extractMAC("? (192.168.0.1) at e4:48:c7:ee:28:c2 [ether] on wlan0"));
	}
}
