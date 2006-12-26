/**
 * 
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.Labels;
import junit.framework.TestCase;

/**
 * PingerRegistryImplTest
 *
 * @author Anton Keks
 */
public class PingerRegistryImplTest extends TestCase {

	public void testGetRegisteredNames() throws Exception {
		String[] names = new PingerRegistryImpl().getRegisteredNames();
		assertNotNull(names);
		for (int i = 0; i < names.length; i++) {
			assertNotNull(Labels.getLabel(names[i]));
		}
	}
	
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
