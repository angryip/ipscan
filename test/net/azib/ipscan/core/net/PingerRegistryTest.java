package net.azib.ipscan.core.net;

import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.FetcherException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PingerRegistryTest {
	ScannerConfig config = Config.getConfig().forScanner();
	PingerRegistry registry;

	@Before
	public void setUp() throws Exception {
		registry = new PingerRegistry(config, new ComponentRegistry().init(false));
	}

	@Test
	public void getRegisteredNames() {
		var names = registry.getRegisteredNames();
		assertNotNull(names);
		for (var name : names) {
			assertNotNull(Labels.getLabel(name));
		}
	}

	@Test
	public void createPinger() throws Exception {
		var names = registry.getRegisteredNames();
		for (var name : names) {
			try {
				var pinger = registry.createPinger(name, 0);
				pinger.close();
			}
			catch (FetcherException e) {
				// ignore in case RawSockets cannot be initialized
				// under current conditions
				assertEquals("pingerCreateFailure", e.getMessage());
			}
		}
	}

	@Test
	public void createSelectedPinger() {
		config.selectedPinger = "pinger.tcp";
		assertTrue(registry.createPinger(false) instanceof TCPPinger);
	}

	@Test
	public void checkBackwardCompatibleCreation() {
		assertTrue(registry.createPinger(PingerDefaultConstructor.class, 0) instanceof PingerDefaultConstructor);
		assertTrue(registry.createPinger(PingerWithTimeoutConstructor.class, 0) instanceof PingerWithTimeoutConstructor);
	}

	abstract static class AbstractTestPinger implements Pinger {
		@Override public PingResult ping(ScanningSubject subject, int count) {
			return null;
		}
	}

	public static class PingerDefaultConstructor extends AbstractTestPinger { }

	static class PingerWithTimeoutConstructor extends AbstractTestPinger {
		public PingerWithTimeoutConstructor(int value) {}
	}
}
