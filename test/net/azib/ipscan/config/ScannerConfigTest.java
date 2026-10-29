package net.azib.ipscan.config;

import org.junit.Test;

import java.util.prefs.Preferences;

import static net.azib.ipscan.config.ScannerConfig.DEFAULT_PORT_STRING;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScannerConfigTest {
	private final Preferences preferences = mock(Preferences.class);

	@Test
	public void loadsValidPortString() {
		assertEquals("1-3,5", portStringFor("1-3,5"));
		assertEquals("", portStringFor(""));
	}

	@Test
	public void replacesInvalidPortStringWithDefault() {
		assertEquals(DEFAULT_PORT_STRING, portStringFor("0-65535"));
		assertEquals(DEFAULT_PORT_STRING, portStringFor("0"));
		assertEquals(DEFAULT_PORT_STRING, portStringFor("foo"));
	}

	private String portStringFor(String storedPortString) {
		when(preferences.get("portString", DEFAULT_PORT_STRING)).thenReturn(storedPortString);
		return new ScannerConfig(preferences).portString;
	}
}
