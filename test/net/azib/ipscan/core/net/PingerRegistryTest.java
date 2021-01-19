package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.FetcherException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PingerRegistryTest {
	@Before
	public void setUp() {
		System.setProperty("java.library.path", "../swt/lib");
	}

	@Test
	public void getRegisteredNames() {
		String[] names = new PingerRegistry(null).getRegisteredNames();
		assertNotNull(names);
		for (String name : names) {
			assertNotNull(Labels.getLabel(name));
		}
	}

	@Test
	public void createPinger() throws Exception {
		PingerRegistry registry = new PingerRegistry(null);
		String[] names = registry.getRegisteredNames();
		for (String name : names) {
			try {
				Pinger pinger = registry.createPinger(name, 0);
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
	public void createDefaultPinger() throws Exception {
		ScannerConfig config = Config.getConfig().forScanner();
		PingerRegistry registry = new PingerRegistry(config);
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.createPinger() instanceof UDPPinger);
	}

	@Test
	public void checkSelectedPinger() {
		ScannerConfig config = Config.getConfig().forScanner();
		PingerRegistry registry = new PingerRegistry(config);

		config.selectedPinger = "pinger.udp";
		assertTrue(registry.checkSelectedPinger());

		config.selectedPinger = "pinger.tcp";
		assertTrue(registry.checkSelectedPinger());

		checkWrongPinger(config, registry, PingerWithoutConstructor.class);
		checkWrongPinger(config, registry, PingerWithIncorrectConstructor.class);
		checkWrongPinger(config, registry, PingerWithWrongPing.class);
	}

	private void checkWrongPinger(ScannerConfig config, PingerRegistry registry, Class<? extends Pinger> wrongPingerClass) {
		final String name = "pinger.icmp." + wrongPingerClass.getName();
		registry.pingers.put(name, wrongPingerClass);
		config.selectedPinger = name;

		assertFalse(registry.checkSelectedPinger());

		String expectedPinger = Platform.WINDOWS ? "pinger.windows" : Platform.MAC_OS ? "pinger.java" : "pinger.combined";
		assertEquals(expectedPinger, config.selectedPinger);
	}

	abstract static class AbstractWrongPinger implements Pinger {
		@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
			return null;
		}

		@Override public void close() {
		}
	}

	static class PingerWithoutConstructor extends AbstractWrongPinger {
	}

	public static class PingerWithIncorrectConstructor extends AbstractWrongPinger {
		public PingerWithIncorrectConstructor() {
		}
	}

	static class PingerWithWrongPing extends AbstractWrongPinger {
		public PingerWithWrongPing(int value) {
		}

		@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
			throw new IOException("This pinger will not work!");
		}
	}
}
