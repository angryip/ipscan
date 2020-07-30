package net.azib.ipscan.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Anton Keks
 */
public class ConfigTest {
	Config config = Config.getConfig();

	@Test
	public void locale() {
		config.language = "et";
		assertEquals(config.getLocale().toString(), "et");
	}

	@Test
	public void localeWithRegion() {
		config.language = "pt_BR";
		assertEquals(config.getLocale().toString(), "pt_BR");
	}

	@Test
	public void testGetters() {
		assertNotNull(config.getPreferences());
		assertNotNull(config.forScanner());
		assertNotNull(config.forGUI());
		assertNotNull(config.forFavorites());
		assertNotNull(config.forOpeners());
	}
}
