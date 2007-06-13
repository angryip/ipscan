/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import net.azib.ipscan.core.ScanningSubject;

/**
 * HostnameFetcherTest
 *
 * @author anton
 */
public class HostnameFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new HostnameFetcher();
	}
	
	@Test
	public void testScan() throws UnknownHostException {
		// Some of these tests are run inside of if's to prevent their failing on certain network configurations
		if (!InetAddress.getLocalHost().getCanonicalHostName().equals(InetAddress.getLocalHost().getHostAddress()))
			assertEquals(InetAddress.getLocalHost().getCanonicalHostName(), fetcher.scan(new ScanningSubject(InetAddress.getLocalHost())));
		InetAddress wwweeAddress = InetAddress.getByName("194.204.33.30");
		if (!wwweeAddress.equals("194.204.33.30"))
			assertEquals(wwweeAddress.getCanonicalHostName(), fetcher.scan(new ScanningSubject(wwweeAddress)));
		assertNull(fetcher.scan(new ScanningSubject(InetAddress.getByName("255.255.255.255"))));
		assertNull(fetcher.scan(new ScanningSubject(InetAddress.getByName("172.13.66.254"))));
	}
}
