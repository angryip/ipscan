/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.assertNotNull;

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
	public void testId() {
		assertNotNull(fetcher.getId());
	}
	
	@Test
	public void testName() {
		assertNotNull(fetcher.getName());
	}

	@Test
	public void testFullName() {
		assertNotNull(fetcher.getFullName());
	}
}
