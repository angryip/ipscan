/**
 * 
 */
package net.azib.ipscan.config;

import java.io.File;
import java.util.Iterator;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * OpenersConfigTest
 *
 * @author anton
 */
public class OpenersConfigTest extends TestCase {
	
	private static final String PREFERENCE_NAME = "openers";
	private Preferences preferences;
	private OpenersConfig config;

	protected void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		config = new OpenersConfig(preferences);
	}

	protected void tearDown() throws Exception {
		preferences.removeNode();
	}
	
	public void testAddNoStrings() {
		try {
			config.add("aa", "b");
			fail();
		}
		catch (IllegalArgumentException e) {}
	}
	
	public void testAdd() {
		config.add("Mega favorite", new OpenersConfig.Opener("a@@@0@@@c"));
		assertEquals("a", config.getOpener("Mega favorite").execString);
		assertEquals(false, config.getOpener("Mega favorite").inTerminal);
		assertEquals("c", config.getOpener("Mega favorite").workingDir.getName());
		assertEquals(1, config.size());
	}
	
	public void testOpenerDeserialize() {
		OpenersConfig.Opener o = new OpenersConfig.Opener("uu@@uu@@@1@@@");
		assertEquals("uu@@uu", o.execString);
		assertEquals(true, o.inTerminal);
		assertEquals(null, o.workingDir);
		
		o = new OpenersConfig.Opener("c:\\program files\\mega app\\app.exe@@@0@@@c:\\windoze system");
		assertEquals("c:\\program files\\mega app\\app.exe", o.execString);
		assertEquals(false, o.inTerminal);
		assertEquals("c:\\windoze system", o.workingDir.getName());
	}
	
	public void testLoad() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa@@@1@@@###bb###bbb@@@1@@@");
		OpenersConfig config = new OpenersConfig(preferences);

		assertEquals("aaa", config.getOpener("aa").execString);
		assertEquals("bbb", config.getOpener("bb").execString);
		assertEquals(2, config.size());
	}
	
	public void testOrder() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa@@@1@@@###bb###bbb@@@1@@@");
		OpenersConfig config = new OpenersConfig(preferences);

		Iterator namesIterator = config.iterateNames();
		assertEquals("aa", namesIterator.next());
		assertEquals("bb", namesIterator.next());
		assertFalse(namesIterator.hasNext());
	}
	
	public void testStore() throws Exception {
		config.add("x", new OpenersConfig.Opener("aa", true, null));
		config.add("x y z", new OpenersConfig.Opener("a a", true, new File("zzz z")));
		config.store();
		
		assertEquals("x###aa@@@1@@@###x y z###a a@@@1@@@zzz z", preferences.get(PREFERENCE_NAME, ""));
	}
	
	public void testUpdate() {
		config.add("x", new OpenersConfig.Opener("aa", true, null));
		config.add("y", new OpenersConfig.Opener("bb", false, null));
		config.add("z", new OpenersConfig.Opener("ccc", false, null));
		
		config.update(new String[] {"x", "z"});
		
		Iterator i = config.iterateNames();
		assertEquals("x", (String)i.next());
		assertEquals("z", (String)i.next());
		assertFalse(i.hasNext());
	}

}
