/**
 * 
 */
package net.azib.ipscan.config;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Anton Keks
 */
public class ConfigTest {
	
	@BeforeClass
	public static void globalSetUp() throws Exception {
		Config.initialize();
	}

	@Test
	public void testInitialize() {
		assertNotNull(Config.getPreferences());
		assertNotNull(Config.getGlobal());
	}
}
