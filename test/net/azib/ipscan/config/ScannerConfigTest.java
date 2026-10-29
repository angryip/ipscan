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
	public void migratesLegacyDefaultMaxThreadsToVirtualThreads() {
		assertEquals(0, maxThreadsFor(ScannerConfig.LEGACY_DEFAULT_MAX_THREADS));
	}

	@Test
	public void keepsOtherMaxThreadsValues() {
		assertEquals(10, maxThreadsFor(10));
		assertEquals(500, maxThreadsFor(500));
		assertEquals(0, maxThreadsFor(0));
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

	private int maxThreadsFor(int storedMaxThreads) {
		when(preferences.getInt("maxThreads", 0)).thenReturn(storedMaxThreads);
		return new ScannerConfig(preferences).maxThreads;
	}
}
