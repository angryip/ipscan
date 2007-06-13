/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.*;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestCase for Fetchers.
 * It contains initialization and generic tests for any Fetcher.
 *
 * @author anton
 */
public abstract class AbstractFetcherTestCase {
	
	Fetcher fetcher;
	
	@BeforeClass
	public static void globalSetUp() {
		// some fetchers are Configurable and therefore need an initialized Config
		Config.initialize();
	}
	
	@Before
	public abstract void setUp() throws Exception;

	@Test
	public void testLabel() {
		assertNotNull(Labels.getLabel(fetcher.getLabel()));
	}
	
}
