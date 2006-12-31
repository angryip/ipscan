/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetcher Registry singleton class.
 * Actually, it registers both plugins and builtins.
 *
 * @author anton
 */
public class FetcherRegistryImpl implements FetcherRegistry {
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private Map registeredFetchers;
	/** Selected for scanning Fetcher implementations, keys are fetcher labels, values are Fetcher instances */
	private Map selectedFetchers;
	/** A collection of update listeners - observers of FetcherRegistry */
	private List updateListeners = new ArrayList();
	
	public FetcherRegistryImpl(Fetcher[] registeredFetchers) {
		this.registeredFetchers = new LinkedHashMap(registeredFetchers.length);
		for (int i = 0; i < registeredFetchers.length; i++) {
			this.registeredFetchers.put(registeredFetchers[i].getLabel(), registeredFetchers[i]);
		}
		this.registeredFetchers = Collections.unmodifiableMap(this.registeredFetchers);
		
		// TODO: this should be loaded from config as well as reasonable defaults should be made
		this.selectedFetchers = new LinkedHashMap(this.registeredFetchers);
	}
	
	public void addListener(FetcherRegistryUpdateListener listener) {
		updateListeners.add(listener);
	}

	public Collection getRegisteredFetchers() {
		return registeredFetchers.values();
	}
	
	public Collection getSelectedFetchers() {
		return selectedFetchers.values();
	}

	public int getSelectedFetcherIndex(String label) {
		int index = 0;
		for (Iterator i = selectedFetchers.values().iterator(); i.hasNext();) {
			Fetcher fetcher = (Fetcher)i.next();
			if (fetcher.getLabel().equals(label)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public void updateSelectedFetchers(String[] labels) {
		// rebuild the map (to recreate the new order of elements)
		Map newList = new LinkedHashMap();
		for (int i = 0; i < labels.length; i++) {
			newList.put(labels[i], registeredFetchers.get(labels[i]));
		}
		selectedFetchers = newList;
		
		// invorm observers
		for (Iterator i = updateListeners.iterator(); i.hasNext();) {
			FetcherRegistryUpdateListener listener = (FetcherRegistryUpdateListener) i.next();
			listener.handleUpdateOfSelectedFetchers(this);
		}
	}
	
}
