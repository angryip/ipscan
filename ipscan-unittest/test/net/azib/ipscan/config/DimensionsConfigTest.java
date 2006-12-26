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

	private Preferences preferences;
	private DimensionsConfig config;

	protected void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		config = new DimensionsConfig(preferences);
	}

	protected void tearDown() throws Exception {
		preferences.removeNode();
	}

	public void testGetWindowDimensions() throws Exception {
		config.windowHeight = 1;
		config.windowWidth = 2;
		config.windowTop = 3;
		config.windowLeft = 4;
		assertEquals(new Rectangle(4, 3, 2, 1), config.getWindowBounds());
	}

	public void testStore() throws Exception {
		config.setWindowBounds(new Rectangle(11, 22, 33, 44), false);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));

		config.setWindowBounds(new Rectangle(77, 22, 33, 44), true);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));
	}

	public void testColumnWidths() throws Exception {
		config.setColumnWidth("ABC", 35);
		assertEquals(35, config.getColumnWidth("ABC"));
		assertEquals(35, preferences.getInt("columnWidth.ABC", 0));
	}

}
