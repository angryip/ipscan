package net.azib.ipscan.config;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
