/**
 * 
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.FetcherException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * PingerRegistryTest
 *
 * @author Anton Keks
 */
public class PingerRegistryTest {
	@Before
	public void setUp() {
		System.setProperty("java.library.path", "../swt/lib");
	}

	@Test
	public void getRegisteredNames() throws Exception {
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
	public void checkSelectedPinger() throws Exception {
		ScannerConfig config = Config.getConfig().forScanner();
		PingerRegistry registry = new PingerRegistry(config);
		
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.checkSelectedPinger());

		config.selectedPinger = "pinger.tcp";
		assertTrue(registry.checkSelectedPinger());
	
		registry.pingers.put("pinger.dummy1", DummyPinger1.class);
		config.selectedPinger = "pinger.icmp.dummy1";
		assertFalse(registry.checkSelectedPinger());
		assertEquals("pinger.combined", config.selectedPinger);
		
		registry.pingers.put("pinger.dummy2", DummyPinger2.class);
		config.selectedPinger = "pinger.icmp.dummy2";
		assertFalse(registry.checkSelectedPinger());
		assertEquals("pinger.combined", config.selectedPinger);
	}
	
	public class DummyPinger1 implements Pinger {
		public PingResult ping(ScanningSubject subject, int count) throws IOException {
			throw new IOException("This pinger will not work!");
		}
		
		public void close() throws IOException {
		}
	}

	public class DummyPinger2 implements Pinger {

		public DummyPinger2() {
			throw new RuntimeException("This pinger will not work, can't even create!");
		}

		public PingResult ping(ScanningSubject subject, int count) throws IOException {
			return null;
		}
		
		public void close() throws IOException {
		}
	}
}
