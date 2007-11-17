/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.assertNotNull;
import net.azib.ipscan.config.Labels;

import org.junit.Before;
import org.junit.Test;

/**
 * TestCase for Fetchers.
 * It contains initialization and generic tests for any Fetcher.
 *
 * @author Anton Keks
 */
public abstract class AbstractFetcherTestCase {
	
	Fetcher fetcher;
	
	@Before
	public abstract void setUp() throws Exception;

	@Test
	public void testLabel() {
		assertNotNull(Labels.getLabel(fetcher.getLabel()));
	}
	
}
