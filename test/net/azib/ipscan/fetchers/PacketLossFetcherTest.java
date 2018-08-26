package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;
import org.junit.Before;

/**
 * PingTTLFetcherTest
 *
 * @author Anton Keks
 */
public class PacketLossFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new PacketLossFetcher(null, Config.getConfig().forScanner());
	}

}
