/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;

import junit.framework.TestCase;

/**
 * DimensionsConfigTest
 *
 * @author anton
 */
public class DimensionsConfigTest extends TestCase {
	
	public void testGetWindowDimensions() throws Exception {
		Preferences prefs = Preferences.userRoot().node("ipscan-test");
		
		try {
			DimensionsConfig config = new DimensionsConfig(prefs);
			config.windowHeight = 1;
			config.windowWidth = 2;
			config.windowTop = 3;
			config.windowLeft = 4;
			assertEquals(new Rectangle(4, 3, 2, 1), config.getWindowBounds());
		}
		finally {
			prefs.removeNode();
		}
	}

	public void testStore() throws Exception {
		Preferences prefs = Preferences.userRoot().node("ipscan-test");
		
		try {
			DimensionsConfig config = new DimensionsConfig(prefs);
			
			config.setWindowBounds(new Rectangle(11, 22, 33, 44), false);
			config.store();			
			assertEquals(11, prefs.getInt("windowLeft", 0));

			config.setWindowBounds(new Rectangle(77, 22, 33, 44), true);
			config.store();			
			assertEquals(11, prefs.getInt("windowLeft", 0));
		}
		finally {
			prefs.removeNode();
		}
	}

}
