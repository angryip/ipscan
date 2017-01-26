package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * IPFetcherTest
 *
 * @author Anton Keks
 */
public class IPFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new IPFetcher();
	}
	
	@Test
	public void testScan() throws UnknownHostException {
		assertEquals(InetAddress.getLocalHost().getHostAddress(), fetcher.scan(new ScanningSubject(InetAddress.getLocalHost())).toString());
		assertEquals("255.255.255.255", fetcher.scan(new ScanningSubject(InetAddress.getByName("255.255.255.255"))).toString());
	}

}
