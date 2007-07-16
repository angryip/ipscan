/**
 * 
 */
package net.azib.ipscan.config;

import static org.junit.Assert.assertEquals;

import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * DimensionsConfigTest
 * 
 * @author Anton Keks
 */
public class DimensionsConfigTest {

	private Preferences preferences;
	private DimensionsConfig config;

	@Before
	public void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		preferences.clear();
		config = new DimensionsConfig(preferences);
	}

	@After
	public void tearDown() throws Exception {
		preferences.removeNode();
	}

	@Test
	public void getWindowDimensions() throws Exception {
		config.windowHeight = 1;
		config.windowWidth = 2;
		config.windowTop = 3;
		config.windowLeft = 4;
		assertEquals(new Rectangle(4, 3, 2, 1), config.getWindowBounds());
	}

	@Test
	public void store() throws Exception {
		config.setWindowBounds(new Rectangle(11, 22, 33, 44), false);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));

		config.setWindowBounds(new Rectangle(77, 22, 33, 44), true);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));
	}

	@Test
	public void columnWidths() throws Exception {
		config.setColumnWidth("ABC", 35);
		assertEquals(35, config.getColumnWidth("ABC"));
		assertEquals(35, preferences.getInt("columnWidth.ABC", 0));
	}

}
