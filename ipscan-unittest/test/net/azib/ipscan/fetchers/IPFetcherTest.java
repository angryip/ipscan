/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.azib.ipscan.core.ScanningSubject;

/**
 * IPFetcherTest
 *
 * @author anton
 */
public class IPFetcherTest extends AbstractFetcherTestCase {

	protected void setUp() throws Exception {
		fetcher = new IPFetcher();
	}
	
	public void testScan() throws UnknownHostException {
		assertEquals(InetAddress.getLocalHost().getHostAddress(), fetcher.scan(new ScanningSubject(InetAddress.getLocalHost())));
		assertEquals("255.255.255.255", fetcher.scan(new ScanningSubject(InetAddress.getByName("255.255.255.255"))));
	}

}
