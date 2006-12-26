/**
 * 
 */
package net.azib.ipscan.config;

import junit.framework.TestCase;

/**
 * @author anton
 */
public class ConfigTest extends TestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
		Config.initialize();
	}

	public void testInitialize() {
		assertNotNull(Config.getPreferences());
		assertNotNull(Config.getGlobal());
	}
}
