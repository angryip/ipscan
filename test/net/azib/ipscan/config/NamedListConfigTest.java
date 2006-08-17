/**
 * 
 */
package net.azib.ipscan.config;

import java.util.Iterator;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * NamedListConfigTest
 *
 * @author anton
 */
public class NamedListConfigTest extends TestCase {
	
	private static final String PREFERENCE_NAME = "blah";
	private Preferences preferences;
	private NamedListConfig config;

	protected void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		config = new NamedListConfig(preferences, PREFERENCE_NAME);
	}

	protected void tearDown() throws Exception {
		preferences.removeNode();
	}
	
	public void testAdd() {
		config.add("Mega favorite", "aaa:xxx");
		assertEquals("aaa:xxx", config.get("Mega favorite"));
		assertEquals(1, config.size());
	}
	
	public void testLoad() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa###bb###bbb###cc###ccc");
		NamedListConfig config = new NamedListConfig(preferences, PREFERENCE_NAME);

		assertEquals("aaa", config.get("aa"));
		assertEquals("bbb", config.get("bb"));
		assertEquals("ccc", config.get("cc"));
		assertEquals(3, config.size());
	}
	
	public void testOrder() throws Exception {
		preferences.put(PREFERENCE_NAME, "aa###aaa###bb###bbb###cc###ccc");
		NamedListConfig config = new NamedListConfig(preferences, PREFERENCE_NAME);

		Iterator namesIterator = config.iterateNames();
		assertEquals("aa", namesIterator.next());
		assertEquals("bb", namesIterator.next());
		assertEquals("cc", namesIterator.next());
		assertFalse(namesIterator.hasNext());
	}
	
	public void testStore() throws Exception {
		NamedListConfig config = new NamedListConfig(preferences, PREFERENCE_NAME);
		
		config.add("x", "y");
		config.add("Buga muga x,1,2,3,4,5", "opopo op : , . l ; - # | @@");
		config.add("127.0.0.1", "192.168.2.25");
		config.store();
		
		assertEquals("x###y###Buga muga x,1,2,3,4,5###opopo op : , . l ; - # | @@###127.0.0.1###192.168.2.25", preferences.get(PREFERENCE_NAME, ""));
	}
	
	public void testUpdate() {
		config.add("z", "zzz");
		config.add("y", "yyy");
		config.add("x", "xxx");
		
		config.update(new String[] {"x", "z"});
		
		Iterator i = config.iterateNames();
		assertEquals("xxx", config.get((String)i.next()));
		assertEquals("zzz", config.get((String)i.next()));
		assertFalse(i.hasNext());
	}

}
