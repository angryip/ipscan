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
 * Fetcher registry singleton class.
 * It registers both plugins and builtins.
 *
 * @author Anton Keks
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
		for (Fetcher fetcher : registeredFetchers) {
			this.registeredFetchers.put(fetcher.getId(), fetcher);
		}
		this.registeredFetchers = Collections.unmodifiableMap(this.registeredFetchers);
		
		// now load the preferences to init selected fetchers
		loadSelectedFetchers(preferences);
	}

	private void loadSelectedFetchers(Preferences preferences) {
		String fetcherPrefValue = preferences.get(PREFERENCE_SELECTED_FETCHERS, null);
		if (fetcherPrefValue == null) {
			// no preferences previously saved, use these default values
			selectedFetchers = new LinkedHashMap<String, Fetcher>();
			selectedFetchers.put(IPFetcher.ID, registeredFetchers.get(IPFetcher.ID));
			selectedFetchers.put(PingFetcher.ID, registeredFetchers.get(PingFetcher.ID));
			selectedFetchers.put(HostnameFetcher.ID, registeredFetchers.get(HostnameFetcher.ID));
			selectedFetchers.put(PortsFetcher.ID, registeredFetchers.get(PortsFetcher.ID));
		}
		else {
			String[] fetcherPrefs = fetcherPrefValue.split("###");
			selectedFetchers = new LinkedHashMap<String, Fetcher>(registeredFetchers.size());
			// initialize saved selected fetchers
			for (int i = 0; i < fetcherPrefs.length; i++) {
				Fetcher fetcher = registeredFetchers.get(fetcherPrefs[i]);
				// make sure that this fetcher is registered
				if (fetcher != null) {
					selectedFetchers.put(fetcherPrefs[i], fetcher);
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

	public int getSelectedFetcherIndex(String id) {
		int index = 0;
		for (Fetcher fetcher : selectedFetchers.values()) {
			if (id.equals(fetcher.getId())) {
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
		
		// inform observers
		for (FetcherRegistryUpdateListener listener : updateListeners) {
			listener.handleUpdateOfSelectedFetchers(this);
		}
		
		// save preferences
		saveSelectedFetchers(preferences);
	}
	
}
