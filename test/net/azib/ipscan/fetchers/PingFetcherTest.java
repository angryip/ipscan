/**
 * 
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;

import org.junit.Before;

/**
 * PingFetcherTest
 *
 * @author Anton Keks
 */
public class PingFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new PingFetcher(null, Config.getGlobal());
	}

}
