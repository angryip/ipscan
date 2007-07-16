/**
 * 
 */
package net.azib.ipscan.core.net;

import static org.junit.Assert.*;
import net.azib.ipscan.config.Labels;

import org.junit.Before;
import org.junit.Test;

/**
 * PingerRegistryImplTest
 *
 * @author Anton Keks Keks
 */
public class PingerRegistryImplTest {
	
	@Before
	public void setUp() {
		System.setProperty("java.library.path", "../swt/lib");
	}

	@Test
	public void testGetRegisteredNames() throws Exception {
		String[] names = new PingerRegistryImpl().getRegisteredNames();
		assertNotNull(names);
		for (int i = 0; i < names.length; i++) {
			assertNotNull(Labels.getLabel(names[i]));
		}
	}
	
	@Test
	public void testCreatePinger() throws Exception {
		PingerRegistry registry = new PingerRegistryImpl();
		String[] names = registry.getRegisteredNames();
		for (int i = 0; i < names.length; i++) {
			try {
				Pinger pinger = registry.createPinger(names[i], 0);
				pinger.close();
			}
			catch (RuntimeException e) {
				// ignore IOExceptions in case RawSockets cannot be initialized 
				// under current conditions
				assertTrue(e.getMessage().startsWith("Unable to create pinger"));
			}
		}
	}
}
