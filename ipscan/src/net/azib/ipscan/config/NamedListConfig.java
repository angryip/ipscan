/**
 * 
 */
package net.azib.ipscan.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * This is a generic named list config.
 * Can be used for storing favorites, openers, and other 
 * user-defined configurations.
 *
 * @author anton
 */
public class NamedListConfig {
	
	protected String preferenceName;
	protected Preferences preferences = Config.getPreferences(); 
	protected Map namedList = new LinkedHashMap();

	// package local constructor
	NamedListConfig(Preferences preferences, String preferenceName) {
		this.preferenceName = preferenceName;
		this.preferences = preferences;
		load();
	}
	
	/**
	 * Loads preferences
	 */
	public void load() {
		if (preferences == null) {
			return;
		}
		
		String[] namedListPrefs = preferences.get(preferenceName, "").split("###");
		for (int i = 0; i < namedListPrefs.length; i += 2) {
			if (namedListPrefs[i].length() > 0) {
				namedList.put(namedListPrefs[i], serializeValue(namedListPrefs[i+1]));
			}
		}
	}

	Object serializeValue(String value) {
		return value;
	}

	/**
	 * Stores the currently available named list
	 */
	public void store() {
		StringBuffer sb = new StringBuffer(32);
		for (Iterator i = namedList.entrySet().iterator(); i.hasNext();) {
			Map.Entry e = (Map.Entry) i.next();
			sb.append(e.getKey()).append("###").append(e.getValue()).append("###");
		}
		if (sb.length() > 3) { 
			sb.delete(sb.length() - 3, sb.length());
		}
		preferences.put(preferenceName, sb.toString());
	}

	/**
	 * @param name displayed to the user
	 * @param value to store according to the name
	 */
	public void add(String name, Object value) {
		namedList.put(name, value);
	}

	/**
	 * @param name name
	 * @return stored value
	 */
	public String get(String name) {
		return (String) namedList.get(name);
	}

	/**
	 * @return an Iterator for iterating names of available items
	 */
	public Iterator iterateNames() {
		return namedList.keySet().iterator();
	}

	public int size() {
		return namedList.size();
	}

	/**
	 * Updates the list, retaining only items that are passed in the array.
	 * The order of elements will be the same as in the array.
	 * 
	 * @param names
	 */
	public void update(String[] names) {
		// rebuild the map (to recreate the new order of elements)
		Map newList = new LinkedHashMap();
		for (int i = 0; i < names.length; i++) {
			newList.put(names[i], namedList.get(names[i]));
		}
		namedList = newList;
	}

}
