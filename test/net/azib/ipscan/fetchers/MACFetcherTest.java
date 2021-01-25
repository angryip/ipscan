package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class MACFetcherTest {
	private MACFetcher fetcher = new MACFetcher() {
		@Override protected String resolveMAC(InetAddress address) {
			return "00:01:02:03:04:05";
		}
	};

	@Test
	public void extractMAC() {
		assertEquals("E4:48:C7:EE:28:C2", fetcher.extractMAC("? (192.168.0.1) at e4:48:c7:ee:28:c2 [ether] on wlan0"));
	}

	@Test
	public void extractMACAddsLeadingZeroesOnOsX() {
		assertEquals("04:48:07:EE:28:02", fetcher.extractMAC("? (192.168.0.1) at 4:48:7:ee:28:2 [ether] on wlan0"));
		assertEquals("C4:2C:03:08:1E:89", fetcher.extractMAC("? (10.10.10.96) at c4:2c:3:8:1e:89 on en0 ifscope permanent [ethernet]"));
	}

	@Test
	public void bytesToMAC() {
		assertEquals("", fetcher.bytesToMAC(new byte[0]));
		assertEquals("00:01:02:0D", fetcher.bytesToMAC(new byte[] {0, 1, 2, 13}));
	}

	@Test
	public void redefinedSeparator() throws UnknownHostException {
		ScanningSubject subject = new ScanningSubject(InetAddress.getLocalHost());
		fetcher.separator = ":";
		assertEquals("00:01:02:03:04:05", fetcher.scan(subject));
		assertEquals("00:01:02:03:04:05", subject.getParameter(MACFetcher.ID));
		fetcher.separator = "-";
		assertEquals("00-01-02-03-04-05", fetcher.scan(subject));
		assertEquals("00:01:02:03:04:05", subject.getParameter(MACFetcher.ID));
	}
}
