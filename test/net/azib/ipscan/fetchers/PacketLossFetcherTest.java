package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;
import org.junit.Before;

public class PacketLossFetcherTest extends AbstractFetcherTestCase {
	@Before
	public void setUp() {
		fetcher = new PacketLossFetcher(null, Config.getConfig().forScanner());
	}
}
