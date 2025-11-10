/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.gui.PreferencesDialog;

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

	private final Preferences preferences;
	private final PreferencesDialog preferencesDialog;
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private Map<String, Fetcher> registeredFetchers;
	
	/** Selected for scanning Fetcher implementations, keys are fetcher labels, values are Fetcher instances */
	private Map<String, Fetcher> selectedFetchers;
	
	/** A collection of update listeners - observers of FetcherRegistry */
	private List<FetcherRegistryUpdateListener> updateListeners = new ArrayList<>();
		
	public FetcherRegistry(List<Fetcher> fetchers, Preferences preferences, PreferencesDialog preferencesDialog) {
		this.preferences = preferences;
		this.preferencesDialog = preferencesDialog;

		registeredFetchers = createFetchersMap(fetchers);

		// now load the preferences to init selected fetchers
		loadSelectedFetchers(preferences);
	}

	private Map<String, Fetcher> createFetchersMap(List<Fetcher> fetchers) {
		Map<String, Fetcher> registeredFetchers = new LinkedHashMap<>(fetchers.size());
		for (var fetcher : fetchers) {
			registeredFetchers.put(fetcher.getId(), fetcher);
		}
		return Collections.unmodifiableMap(registeredFetchers);
	}

	private void loadSelectedFetchers(Preferences preferences) {
		var fetcherPrefValue = preferences.get(PREFERENCE_SELECTED_FETCHERS, null);
		if (fetcherPrefValue == null) {
			// no preferences previously saved, use these default values
			selectedFetchers = new LinkedHashMap<>();
			selectedFetchers.put(IPFetcher.ID, registeredFetchers.get(IPFetcher.ID));
			selectedFetchers.put(PingFetcher.ID, registeredFetchers.get(PingFetcher.ID));
			selectedFetchers.put(HostnameFetcher.ID, registeredFetchers.get(HostnameFetcher.ID));
			selectedFetchers.put(PortsFetcher.ID, registeredFetchers.get(PortsFetcher.ID));
		}
		else {
			var fetcherPrefs = fetcherPrefValue.split("###");
			selectedFetchers = new LinkedHashMap<>(registeredFetchers.size());
			// initialize saved selected fetchers
			for (var fetcherPref : fetcherPrefs) {
				var fetcher = registeredFetchers.get(fetcherPref);
				// make sure that this fetcher is registered
				if (fetcher != null) {
					selectedFetchers.put(fetcherPref, fetcher);
				}
			}
		}
	}
	
	private void saveSelectedFetchers(Preferences preferences) {
		var sb = new StringBuilder();
		for (var fetcherName : selectedFetchers.keySet()) {
			sb.append(fetcherName).append("###");
		}
		var value = sb.toString();
		if (value.endsWith("###"))
			value = value.substring(0, value.length() - 3);
		
		preferences.put(PREFERENCE_SELECTED_FETCHERS, value);
	}

  /**
   * Adds a listener to observe FetcherRegistry events, like modification of selected fetchers.
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
   * @return the index, if found, or -1
   */
	public int getSelectedFetcherIndex(String id) {
		var index = 0;
		for (var fetcher : selectedFetchers.values()) {
			if (id.equals(fetcher.getId())) return index;
			index++;
		}
		return -1;
	}

  /**
   * Updates the list, retaining only items that are passed in the array.
   * The order of elements will be the same as in the array.
   */
	public void updateSelectedFetchers(String[] labels) {
		// rebuild the map (to recreate the new order of elements)
		Map<String, Fetcher> newList = new LinkedHashMap<>();
		for (var label : labels) {
			newList.put(label, registeredFetchers.get(label));
		}
		selectedFetchers = newList;
		
		// inform observers
		for (var listener : updateListeners) {
			listener.handleUpdateOfSelectedFetchers(this);
		}
		
		// save preferences
		saveSelectedFetchers(preferences);
	}

  /**
   * Opens preferences editor for the specified fetcher, if possible.
   * @throws FetcherException if preferences editor doesn't exist
   */
	public void openPreferencesEditor(Fetcher fetcher) throws FetcherException {
		var prefsEditorClass = fetcher.getPreferencesClass();
		if (prefsEditorClass == null)
			throw new FetcherException("preferences.notAvailable");

		try {
			var prefs = createFetcherPrefsEditor(prefsEditorClass);
			prefs.openFor(fetcher);
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot instantiate fetcher preference editor: " + prefsEditorClass.getName(), e);
		}
	}

	private FetcherPrefs createFetcherPrefsEditor(Class<? extends FetcherPrefs> prefsClass) throws Exception {
		try {
			var constructor = prefsClass.getConstructor(PreferencesDialog.class);
			return constructor.newInstance(preferencesDialog);
		}
		catch (NoSuchMethodException e) {
			return prefsClass.newInstance();
		}
	}
}
