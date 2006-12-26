/**
 * 
 */
package net.azib.ipscan.fetchers;

import org.junit.Before;

/**
 * PingTTLFetcherTest
 *
 * @author anton
 */
public class PingTTLFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new PingTTLFetcher(null);
	}

}
