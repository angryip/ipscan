package net.azib.ipscan.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

/**
 * OpenersConfigTest
 *
 * @author Anton Keks
 */
public class OpenersConfigTest {
	
	private static final String PREFERENCE_NAME = "openers";
	private Preferences preferences;
	private OpenersConfig config;

	@Before
	public void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		preferences.clear();
		config = new OpenersConfig(preferences);
	}

	@After
	public void tearDown() throws Exception {
		preferences.removeNode();
	}
	
	@Test
	public void testDefaultValues() throws Exception {
		assertTrue(config.size() > 0);
	}
	
	@Test
	public void testAddNoStrings() {
		try {
			config.add("aa", "b");
			fail();
		}
		catch (IllegalArgumentException e) {}
	}
	
	@Test
	public void testAdd() {
		// clear default values
		config.update(new String[] {});
		
		config.add("Mega favorite", new OpenersConfig.Opener("a@@@0@@@c"));
		assertEquals("a", config.getOpener("Mega favorite").execString);
		assertEquals(false, config.getOpener("Mega favorite").inTerminal);
		assertEquals("c", config.getOpener("Mega favorite").workingDir.getName());
		assertEquals(1, config.size());
	}
	
	@Test
	public void testOpenerDeserialize() {
		OpenersConfig.Opener o = new OpenersConfig.Opener("uu@@uu@@@1@@@");
		assertEquals("uu@@uu", o.execString);
		assertEquals(true, o.inTerminal);
		assertEquals(null, o.workingDir);
		
		o = new OpenersConfig.Opener("c:\\program files\\mega app\\app.exe@@@0@@@c:\\windoze system");
		assertEquals("c:\\program files\\mega app\\app.exe", o.execString);
		assertEquals(false, o.inTerminal);
		assertEquals("c:\\windoze system", o.workingDir.toString());
	}
	
	@Test
	public void testLoad() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa@@@1@@@###bb###bbb@@@1@@@");
		OpenersConfig config = new OpenersConfig(preferences);

		assertEquals("aaa", config.getOpener("aa").execString);
		assertEquals("bbb", config.getOpener("bb").execString);
		assertEquals(2, config.size());
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testOrder() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa@@@1@@@###bb###bbb@@@1@@@");
		OpenersConfig config = new OpenersConfig(preferences);

		Iterator namesIterator = config.iterator();
		assertEquals("aa", namesIterator.next());
		assertEquals("bb", namesIterator.next());
		assertFalse(namesIterator.hasNext());
	}
	
	@Test
	public void testStore() throws Exception {
		// clear default values
		config.update(new String[] {});
		
		config.add("x", new OpenersConfig.Opener("aa", true, null));
		config.add("x y z", new OpenersConfig.Opener("a a", true, new File("zzz z")));
		config.store();
		
		assertEquals("x###aa@@@1@@@###x y z###a a@@@1@@@zzz z", preferences.get(PREFERENCE_NAME, ""));
	}
	
	@Test @SuppressWarnings("unchecked")
	public void testUpdate() {
		config.add("x", new OpenersConfig.Opener("aa", true, null));
		config.add("y", new OpenersConfig.Opener("bb", false, null));
		config.add("z", new OpenersConfig.Opener("ccc", false, null));
		
		config.update(new String[] {"x", "z"});
		
		Iterator i = config.iterator();
		assertEquals("x", i.next());
		assertEquals("z", i.next());
		assertFalse(i.hasNext());
	}

}
