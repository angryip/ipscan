/**
 * 
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;

import org.junit.Before;

/**
 * PortsFetcherTest
 *
 * @author anton
 */
public class PortsFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new PortsFetcher(Config.getGlobal());
	}

}
