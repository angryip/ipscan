/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Fetcher Registry singleton class.
 * Actually, it registers both plugins and builtins.
 *
 * @author anton
 */
public class FetcherRegistryImpl implements FetcherRegistry {
	
	static final String PREFERENCE_SELECTED_FETCHERS = "selectedFetchers";
	private Preferences preferences;
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private Map<String, Fetcher> registeredFetchers;
	/** Selected for scanning Fetcher implementations, keys are fetcher labels, values are Fetcher instances */
	private Map<String, Fetcher> selectedFetchers;
	/** A collection of update listeners - observers of FetcherRegistry */
	private List<FetcherRegistryUpdateListener> updateListeners = new ArrayList<FetcherRegistryUpdateListener>();
	
	public FetcherRegistryImpl(Fetcher[] registeredFetchers, Preferences preferences) {
		this.preferences = preferences;
		
		this.registeredFetchers = new LinkedHashMap<String, Fetcher>(registeredFetchers.length);
		for (int i = 0; i < registeredFetchers.length; i++) {
			this.registeredFetchers.put(registeredFetchers[i].getLabel(), registeredFetchers[i]);
		}
		this.registeredFetchers = Collections.unmodifiableMap(this.registeredFetchers);
		
		// now load the preferences to init selected fetchers
		loadSelectedFetchers(preferences);
	}

	private void loadSelectedFetchers(Preferences preferences) {
		String fetcherPrefValue = preferences.get(PREFERENCE_SELECTED_FETCHERS, null);
		if (fetcherPrefValue == null) {
			// no preferences previously saved, use these default values
			this.selectedFetchers = new LinkedHashMap<String, Fetcher>(this.registeredFetchers);
		}
		else {
			String[] fetcherPrefs = fetcherPrefValue.split("###");
			this.selectedFetchers = new LinkedHashMap<String, Fetcher>(this.registeredFetchers.size());
			// initialize saved selected fetchers
			for (int i = 0; i < fetcherPrefs.length; i++) {
				if (fetcherPrefs[i].length() > 0) {
					this.selectedFetchers.put(fetcherPrefs[i], this.registeredFetchers.get(fetcherPrefs[i]));
				}
			}
		}
	}
	
	private void saveSelectedFetchers(Preferences preferences) {
		StringBuffer sb = new StringBuffer();
		for (String fetcherName : selectedFetchers.keySet()) {
			sb.append(fetcherName).append("###");
		}
		String value = sb.toString();
		if (value.endsWith("###"))
			value = value.substring(0, value.length() - 3);
		
		preferences.put(PREFERENCE_SELECTED_FETCHERS, value);
	}

	public void addListener(FetcherRegistryUpdateListener listener) {
		updateListeners.add(listener);
	}

	public Collection<Fetcher> getRegisteredFetchers() {
		return registeredFetchers.values();
	}
	
	public Collection<Fetcher> getSelectedFetchers() {
		return selectedFetchers.values();
	}

	public int getSelectedFetcherIndex(String label) {
		int index = 0;
		for (Fetcher fetcher : selectedFetchers.values()) {
			if (fetcher.getLabel().equals(label)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public void updateSelectedFetchers(String[] labels) {
		// rebuild the map (to recreate the new order of elements)
		Map<String, Fetcher> newList = new LinkedHashMap<String, Fetcher>();
		for (int i = 0; i < labels.length; i++) {
			newList.put(labels[i], registeredFetchers.get(labels[i]));
		}
		selectedFetchers = newList;
		
		// invorm observers
		for (FetcherRegistryUpdateListener listener : updateListeners) {
			listener.handleUpdateOfSelectedFetchers(this);
		}
		
		// save preferences
		saveSelectedFetchers(preferences);
	}
	
}
