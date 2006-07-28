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
	
	public void testAdd() {
		FavoritesConfig favorites = new FavoritesConfig() {
			void load() {
				// do nothing
			}			
		};
		favorites.add("Mega favorite", "aaa:xxx");
		assertEquals("aaa:xxx", favorites.get("Mega favorite"));
		assertEquals(1, favorites.size());
	}
	
	public void testLoad() throws Exception {
		Preferences prefs = Preferences.userRoot().node("ipscan-test");
		
		try {
			prefs.put("favorites", "aa###aaa###bb###bbb###cc###ccc");
			FavoritesConfig favorites = new FavoritesConfig(prefs);

			assertEquals("aaa", favorites.get("aa"));
			assertEquals("bbb", favorites.get("bb"));
			assertEquals("ccc", favorites.get("cc"));
			assertEquals(3, favorites.size());
		}
		finally {
			prefs.removeNode();
		}
	}
	
	public void testOrder() throws Exception {
		Preferences prefs = Preferences.userRoot().node("ipscan-test");
		
		try {
			prefs.put("favorites", "aa###aaa###bb###bbb###cc###ccc");
			FavoritesConfig favorites = new FavoritesConfig(prefs);

			Iterator favoriteNames = favorites.iterateNames();
			assertEquals("aa", favoriteNames.next());
			assertEquals("bb", favoriteNames.next());
			assertEquals("cc", favoriteNames.next());
			assertFalse(favoriteNames.hasNext());
		}
		finally {
			prefs.removeNode();
		}
	}
	
	public void testStore() throws Exception {
		Preferences prefs = Preferences.userRoot().node("ipscan-test");
		
		try {
			FavoritesConfig favorites = new FavoritesConfig(prefs);
			
			favorites.add("x", "y");
			favorites.add("Buga muga x,1,2,3,4,5", "opopo op : , . l ; - # | @@");
			favorites.add("127.0.0.1", "192.168.2.25");
			favorites.store();
			
			assertEquals("x###y###Buga muga x,1,2,3,4,5###opopo op : , . l ; - # | @@###127.0.0.1###192.168.2.25", prefs.get("favorites", ""));
		}
		finally {
			prefs.removeNode();
		}
	}
	
	public void testUpdate() {
		FavoritesConfig favorites = new FavoritesConfig();
		favorites.add("z", "zzz");
		favorites.add("y", "yyy");
		favorites.add("x", "xxx");
		
		favorites.update(new String[] {"x", "z"});
		
		Iterator i = favorites.iterateNames();
		assertEquals("xxx", favorites.get((String)i.next()));
		assertEquals("zzz", favorites.get((String)i.next()));
		assertFalse(i.hasNext());
	}

}
