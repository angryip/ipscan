package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
	public void resolveForReal() throws UnknownHostException {
		// Some of these tests are run inside of if's to prevent their failing on certain network configurations
		if (!InetAddress.getLocalHost().getCanonicalHostName().equals(InetAddress.getLocalHost().getHostAddress()))
			assertEquals(InetAddress.getLocalHost().getCanonicalHostName(), fetcher.scan(new ScanningSubject(InetAddress.getLocalHost())));
		
		try {
			InetAddress address = InetAddress.getByName("era.ee");
			assertEquals("era.ee", fetcher.scan(new ScanningSubject(address)));
		}
		catch (UnknownHostException e) { /* ignore - test is running in off-line environment */ }
		
		InetAddress inexistentAddress = InetAddress.getByName("192.168.253.253");
		if (inexistentAddress.getHostName().equals("192.168.253.253"))
			assertNull(fetcher.scan(new ScanningSubject(inexistentAddress)));			
	}
}
