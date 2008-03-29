/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.values.NotAvailableValue;

import org.junit.Before;
import org.junit.Test;

/**
 * PortsFetcherTest
 *
 * @author Anton Keks
 */
public class PortsFetcherTest extends AbstractFetcherTestCase {
	
	private ScannerConfig config;

	@Before
	public void setUp() throws Exception {
		config = createMock(ScannerConfig.class);
		fetcher = new PortsFetcher(config);
	}
	
	@Test
	public void numberOfPortsInFullName() throws Exception {
		config.portString = "";
		assertEquals(fetcher.getName() + " " + NotAvailableValue.INSTANCE, fetcher.getFullName());

		config.portString = "1-3";
		assertEquals(fetcher.getName() + " [3]", fetcher.getFullName());
	}

}
