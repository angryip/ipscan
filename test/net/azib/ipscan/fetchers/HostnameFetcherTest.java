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
 * @author Anton Keks
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
		
		try {
			InetAddress googleAddress = InetAddress.getByName("www.google.com");
			assertEquals(googleAddress.getCanonicalHostName(), fetcher.scan(new ScanningSubject(googleAddress)));
		}
		catch (UnknownHostException e) { /* ignore - test is running in off-line environment */ }
		
		InetAddress inexistentAddress = InetAddress.getByName("192.168.253.253");
		if (inexistentAddress.getHostName().equals("192.168.253.253")) {
			assertNull(fetcher.scan(new ScanningSubject(inexistentAddress)));			
		}
	}
}
