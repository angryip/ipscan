/**
 * 
 */
package net.azib.ipscan.fetchers;

import org.junit.Before;

/**
 * PingFetcherTest
 *
 * @author anton
 */
public class PingFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new PingFetcher(null);
	}

}
