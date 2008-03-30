/**
 * 
 */
package net.azib.ipscan.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @author Anton Keks
 */
public class ConfigTest {
	
	@Test
	public void testGetters() {
		Config config = Config.getConfig();
		assertNotNull(config.getPreferences());
		assertNotNull(config.forScanner());
		assertNotNull(config.forGUI());
		assertNotNull(config.forFavorites());
		assertNotNull(config.forOpeners());
	}
}
