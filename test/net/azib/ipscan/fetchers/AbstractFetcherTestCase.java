/**
 * 
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import junit.framework.TestCase;

/**
 * TestCase for Fetchers.
 * It contains initialization and generic tests for any Fetcher.
 *
 * @author anton
 */
public abstract class AbstractFetcherTestCase extends TestCase {
	
	Fetcher fetcher;
	
	static {
		// some fetchers are Configurale and therefore need an initialized Config
		Config.initialize();
	}
	
	protected abstract void setUp() throws Exception;

	public void testLabel() {
		assertNotNull(Labels.getInstance().getString(fetcher.getLabel()));
	}
	
}
