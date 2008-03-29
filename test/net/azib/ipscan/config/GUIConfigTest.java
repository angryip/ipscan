/**
 * 
 */
package net.azib.ipscan.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.prefs.Preferences;

import net.azib.ipscan.fetchers.Fetcher;

import org.eclipse.swt.graphics.Rectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * DimensionsConfigTest
 * 
 * @author Anton Keks
 */
public class GUIConfigTest {

	private Preferences preferences;
	private GUIConfig config;

	@Before
	public void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		preferences.clear();
		config = new GUIConfig(preferences);
	}

	@After
	public void tearDown() throws Exception {
		preferences.removeNode();
	}

	@Test
	public void setMainWindowDimensions() throws Exception {
		Rectangle bounds1 = new Rectangle(4, 3, 2, 1);
		config.setMainWindowBounds(bounds1, false);
		assertFalse(config.isMainWindowMaximized);
		assertEquals(bounds1, config.getMainWindowBounds());
		
		Rectangle bounds2 = new Rectangle(1, 2, 3, 4);
		config.setMainWindowBounds(bounds2, true);
		assertTrue(config.isMainWindowMaximized);
		assertEquals(bounds1, config.getMainWindowBounds());
	}

	@Test
	public void store() throws Exception {
		config.setMainWindowBounds(new Rectangle(11, 22, 33, 44), false);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));

		config.setMainWindowBounds(new Rectangle(77, 22, 33, 44), true);
		config.store();
		assertEquals(11, preferences.getInt("windowLeft", 0));
	}

	@Test
	public void columnWidths() throws Exception {
		Fetcher fetcher = createMock(Fetcher.class);
		expect(fetcher.getId()).andReturn("fetcher.abc").anyTimes();
		replay(fetcher);
		
		config.setColumnWidth(fetcher, 35);
		assertEquals(35, config.getColumnWidth(fetcher));
		assertEquals(35, preferences.getInt("columnWidth." + fetcher.getId(), 0));
	}

}
