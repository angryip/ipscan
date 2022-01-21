package net.azib.ipscan.core.net;

import net.azib.ipscan.config.*;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.FetcherException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PingerRegistryTest {
	ScannerConfig config = Config.getConfig().forScanner();
	PingerRegistry registry;

	@Before
	public void setUp() throws Exception {
		System.setProperty("java.library.path", "../swt/lib");
		registry = new PingerRegistry(config, new ComponentRegistry().init(false));
	}

	@Test
	public void getRegisteredNames() {
		String[] names = registry.getRegisteredNames();
		assertNotNull(names);
		for (String name : names) {
			assertNotNull(Labels.getLabel(name));
		}
	}

	@Test
	public void createPinger() throws Exception {
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
	public void createDefaultPinger() {
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.createPinger(false) instanceof UDPPinger);
	}

	@Test
	public void checkSelectedPinger() {
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.checkSelectedPinger());

		config.selectedPinger = "pinger.tcp";
		assertTrue(registry.checkSelectedPinger());

		checkWrongPinger(PingerWithoutConstructor.class);
		checkWrongPinger(PingerWithWrongPing.class);
	}

	private void checkWrongPinger(Class<? extends Pinger> wrongPingerClass) {
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
	}

	static class PingerWithoutConstructor extends AbstractWrongPinger {
	}

	static class PingerWithWrongPing extends AbstractWrongPinger {
		public PingerWithWrongPing(int value) {
		}

		@Override public PingResult ping(ScanningSubject subject, int count) throws IOException {
			throw new IOException("This pinger will not work!");
		}
	}
}
