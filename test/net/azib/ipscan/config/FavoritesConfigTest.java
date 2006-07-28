/**
 * 
 */
package net.azib.ipscan.config;

import java.util.Iterator;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

/**
 * FavoritesConfigTest
 *
 * @author anton
 */
public class FavoritesConfigTest extends TestCase {
	
	private Preferences preferences;
	private FavoritesConfig config;

	protected void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		config = new FavoritesConfig(preferences);
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
		preferences.put("favorites", "aa###aaa###bb###bbb###cc###ccc");
		FavoritesConfig favorites = new FavoritesConfig(preferences);

		assertEquals("aaa", favorites.get("aa"));
		assertEquals("bbb", favorites.get("bb"));
		assertEquals("ccc", favorites.get("cc"));
		assertEquals(3, favorites.size());
	}
	
	public void testOrder() throws Exception {
		preferences.put("favorites", "aa###aaa###bb###bbb###cc###ccc");
		FavoritesConfig favorites = new FavoritesConfig(preferences);

		Iterator favoriteNames = favorites.iterateNames();
		assertEquals("aa", favoriteNames.next());
		assertEquals("bb", favoriteNames.next());
		assertEquals("cc", favoriteNames.next());
		assertFalse(favoriteNames.hasNext());
	}
	
	public void testStore() throws Exception {
		FavoritesConfig favorites = new FavoritesConfig(preferences);
		
		favorites.add("x", "y");
		favorites.add("Buga muga x,1,2,3,4,5", "opopo op : , . l ; - # | @@");
		favorites.add("127.0.0.1", "192.168.2.25");
		favorites.store();
		
		assertEquals("x###y###Buga muga x,1,2,3,4,5###opopo op : , . l ; - # | @@###127.0.0.1###192.168.2.25", preferences.get("favorites", ""));
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
