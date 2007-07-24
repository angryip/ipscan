/**
 * 
 */
package net.azib.ipscan.core.net;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.FetcherException;

import org.junit.Before;
import org.junit.Test;

/**
 * PingerRegistryImplTest
 *
 * @author Anton Keks
 */
public class PingerRegistryImplTest {
	
	@Before
	public void setUp() {
		System.setProperty("java.library.path", "../swt/lib");
	}

	@Test
	public void testGetRegisteredNames() throws Exception {
		String[] names = new PingerRegistryImpl(null).getRegisteredNames();
		assertNotNull(names);
		for (int i = 0; i < names.length; i++) {
			assertNotNull(Labels.getLabel(names[i]));
		}
	}
	
	@Test
	public void testCreatePinger() throws Exception {
		PingerRegistryImpl registry = new PingerRegistryImpl(null);
		String[] names = registry.getRegisteredNames();
		for (int i = 0; i < names.length; i++) {
			try {
				Pinger pinger = registry.createPinger(names[i], 0);
				pinger.close();
			}
			catch (FetcherException e) {
				// ignore in case RawSockets cannot be initialized 
				// under current conditions
				assertEquals("pingerCreateFailure", e.getMessage());
			}
		}
	}
	
	public void testCreateDefaultPinger() throws Exception {
		Config.initialize();
		GlobalConfig config = Config.getGlobal();
		PingerRegistry registry = new PingerRegistryImpl(config);
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.createPinger() instanceof UDPPinger);
	}
	
	@Test
	public void checkSelectedPinger() throws Exception {
		Config.initialize();
		GlobalConfig config = Config.getGlobal();
		PingerRegistryImpl registry = new PingerRegistryImpl(config);
		
		config.selectedPinger = "pinger.udp";
		assertTrue(registry.checkSelectedPinger());

		config.selectedPinger = "pinger.tcp";
		assertTrue(registry.checkSelectedPinger());
	
		registry.pingers.put("pinger.dummy1", DummyPinger1.class);
		config.selectedPinger = "pinger.icmp.dummy1";
		assertFalse(registry.checkSelectedPinger());
		assertEquals("pinger.udp", config.selectedPinger);
		
		registry.pingers.put("pinger.dummy2", DummyPinger2.class);
		config.selectedPinger = "pinger.icmp.dummy2";
		assertFalse(registry.checkSelectedPinger());
		assertEquals("pinger.udp", config.selectedPinger);
	}
	
	public class DummyPinger1 implements Pinger {
		public PingResult ping(InetAddress address, int count) throws IOException {
			throw new IOException("This pinger will not work!");
		}
		
		public void close() throws IOException {
		}
	}

	public class DummyPinger2 implements Pinger {

		public DummyPinger2() {
			throw new RuntimeException("This pinger will not work, can't even create!");
		}

		public PingResult ping(InetAddress address, int count) throws IOException {
			return null;
		}
		
		public void close() throws IOException {
		}
	}
}
