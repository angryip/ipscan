/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import javax.inject.Inject;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * Fetcher registry singleton class.
 * It registers both plugins and builtins.
 *
 * @author Anton Keks
 */
public class FetcherRegistry {
	static final String PREFERENCE_SELECTED_FETCHERS = "selectedFetchers";
	private Preferences preferences;
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private Map<String, Fetcher> registeredFetchers;
	
	/** Selected for scanning Fetcher implementations, keys are fetcher labels, values are Fetcher instances */
	private Map<String, Fetcher> selectedFetchers;
	
	/** A collection of update listeners - observers of FetcherRegistry */
	private List<FetcherRegistryUpdateListener> updateListeners = new ArrayList<FetcherRegistryUpdateListener>();
		
	@Inject public FetcherRegistry(List<Fetcher> registeredFetchers, Preferences preferences) {
		this.preferences = preferences;

		this.registeredFetchers = new LinkedHashMap<String, Fetcher>(registeredFetchers.size());
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
			for (String fetcherPref : fetcherPrefs) {
				Fetcher fetcher = registeredFetchers.get(fetcherPref);
				// make sure that this fetcher is registered
				if (fetcher != null) {
					selectedFetchers.put(fetcherPref, fetcher);
				}
			}
		}
	}
	
	private void saveSelectedFetchers(Preferences preferences) {
		StringBuilder sb = new StringBuilder();
		for (String fetcherName : selectedFetchers.keySet()) {
			sb.append(fetcherName).append("###");
		}
		String value = sb.toString();
		if (value.endsWith("###"))
			value = value.substring(0, value.length() - 3);
		
		preferences.put(PREFERENCE_SELECTED_FETCHERS, value);
	}

  /**
   * Adds a listener to observe FetcherRegistry events, like modification of selected fetchers.
   * @param listener
   */
	public void addListener(FetcherRegistryUpdateListener listener) {
		updateListeners.add(listener);
	}

  /**
   * @return a List of all registered Fetchers
   */
	public Collection<Fetcher> getRegisteredFetchers() {
		return registeredFetchers.values();
	}

  /**
   * @return a List of selected Fetchers only
   */
	public Collection<Fetcher> getSelectedFetchers() {
		return selectedFetchers.values();
	}

  /**
   * Searches for selected fetcher with the given label
   * @param id
   * @return the index, if found, or -1
   */
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

  /**
   * Updates the list, retaining only items that are passed in the array.
   * The order of elements will be the same as in the array.
   * @param labels
   */
	public void updateSelectedFetchers(String[] labels) {
		// rebuild the map (to recreate the new order of elements)
		Map<String, Fetcher> newList = new LinkedHashMap<String, Fetcher>();
		for (String label : labels) {
			newList.put(label, registeredFetchers.get(label));
		}
		selectedFetchers = newList;
		
		// inform observers
		for (FetcherRegistryUpdateListener listener : updateListeners) {
			listener.handleUpdateOfSelectedFetchers(this);
		}
		
		// save preferences
		saveSelectedFetchers(preferences);
	}

  /**
   * Opens preferences editor for the specified fetcher, if possible.
   * @param fetcher
   * @throws FetcherException if preferences editor doesn't exist
   */
	public void openPreferencesEditor(Fetcher fetcher) throws FetcherException {
		Class<? extends FetcherPrefs> prefsClass = fetcher.getPreferencesClass();
		if (prefsClass == null)
			throw new FetcherException("preferences.notAvailable");

		try {
			FetcherPrefs prefs = prefsClass.newInstance();
			prefs.openFor(fetcher);
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot instantiate fetcher preference editor: " + prefsClass.getName());
		}
	}
}
