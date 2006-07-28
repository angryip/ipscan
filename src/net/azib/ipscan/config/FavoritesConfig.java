/**
 * 
 */
package net.azib.ipscan.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * FavoritesConfig
 *
 * @author anton
 */
public class FavoritesConfig {
	
	private Preferences preferences = Config.getPreferences(); 
	private Map favorites = new LinkedHashMap();

	public FavoritesConfig() {
		load();
	}

	/**
	 * This constructor is for tests
	 * @param preferences
	 */
	FavoritesConfig(Preferences preferences) {
		this.preferences = preferences;
		load();
	}
	
	/**
	 * Loads preferences
	 */
	void load() {
		if (preferences == null) {
			return;
		}
		
		String[] favoritePrefs = preferences.get("favorites", "").split("###");
		for (int i = 0; i < favoritePrefs.length; i += 2) {
			if (favoritePrefs[i].length() > 0) {
				favorites.put(favoritePrefs[i], favoritePrefs[i+1]);
			}
		}
	}

	/**
	 * Stores the currently available favorites
	 */
	public void store() {
		StringBuffer sb = new StringBuffer(32);
		for (Iterator i = favorites.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			sb.append(e.getKey()).append("###").append(e.getValue()).append("###");
		}
		if (sb.length() > 3) { 
			sb.delete(sb.length() - 3, sb.length());
		}
		preferences.put("favorites", sb.toString());
	}

	/**
	 * @param name favorite name
	 * @param feederInfo feederInfo to restore feeder state
	 */
	public void add(String name, String feederInfo) {
		favorites.put(name, feederInfo);
	}

	/**
	 * @param name favorite name
	 * @return feederInfo string
	 */
	public String get(String name) {
		return (String) favorites.get(name);
	}

	/**
	 * @return an Iterator for iterating names of available favorites
	 */
	public Iterator iterateNames() {
		return favorites.keySet().iterator();
	}

	public int size() {
		return favorites.size();
	}

	/**
	 * Updates favorites, retaining only those that are passed in the array.
	 * The order of elements will be the same as in the array.
	 * 
	 * @param names
	 */
	public void update(String[] names) {
		// rebuild the map (to recreate the new order of elements)
		Map newFavorites = new LinkedHashMap();
		for (int i = 0; i < names.length; i++) {
			newFavorites.put(names[i], favorites.get(names[i]));
		}
		favorites = newFavorites;
	}

}
