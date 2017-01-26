package net.azib.ipscan.config;

import net.azib.ipscan.fetchers.Fetcher;
import org.eclipse.swt.graphics.Point;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.prefs.Preferences;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
		Point size1 = new Point(2, 1);
		config.setMainWindowSize(size1, false);
		assertFalse(config.isMainWindowMaximized);
		assertEquals(size1, config.getMainWindowSize());
		
		Point size2 = new Point(3, 4);
		config.setMainWindowSize(size2, true);
		assertTrue(config.isMainWindowMaximized);
		assertEquals(size1, config.getMainWindowSize());
	}

	@Test
	public void store() throws Exception {
		config.setMainWindowSize(new Point(33, 44), false);
		config.store();
		assertEquals(33, preferences.getInt("windowWidth", 0));

		config.setMainWindowSize(new Point(55, 66), true);
		config.store();
		assertEquals(33, preferences.getInt("windowWidth", 0));
	}

	@Test
	public void columnWidths() throws Exception {
		Fetcher fetcher = mock(Fetcher.class);
		when(fetcher.getId()).thenReturn("fetcher.abc");

		config.setColumnWidth(fetcher, 35);
		assertEquals(35, config.getColumnWidth(fetcher));
		assertEquals(35, preferences.getInt("columnWidth." + fetcher.getId(), 0));
	}
}
