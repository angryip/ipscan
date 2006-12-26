/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.ArrayList;
import java.util.Arrays;
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
	private List registeredFetchers;
	/** Selected for scanning Fetcher implementations, keys are fetcher labels, values are Fetcher instances */
	private Map selectedFetchers;
	/** A collection of update listeners - observers of FetcherRegistry */
	private List updateListeners = new ArrayList();
	
	public FetcherRegistryImpl(Fetcher[] registeredFetchers) {
		this.registeredFetchers = Arrays.asList(registeredFetchers);
		this.registeredFetchers = Collections.unmodifiableList(this.registeredFetchers);
		
		// TODO: this should be loaded from config as well as reasonable defaults should be made
		this.selectedFetchers = new LinkedHashMap();
		for (Iterator i = this.registeredFetchers.iterator(); i.hasNext();) {
			Fetcher fetcher = (Fetcher) i.next();
			this.selectedFetchers.put(fetcher.getLabel(), fetcher);
		}
	}
	
	public void addListener(FetcherRegistryUpdateListener listener) {
		updateListeners.add(listener);
	}

	public Collection getRegisteredFetchers() {
		return registeredFetchers;
	}
	
	public Collection getSelectedFetchers() {
		return selectedFetchers.values();
	}

	public int getSelectedFetcherIndex(String label) {
		int index = -1;
		for (Iterator i = selectedFetchers.values().iterator(); i.hasNext();) {
			if (((Fetcher)i.next()).getLabel().equals(label))
				break;
			index++;
			
		}
		return index;
	}
	
	public void updateSelectedFetchers(String[] labels) {
		// rebuild the map (to recreate the new order of elements)
		Map newList = new LinkedHashMap();
		for (int i = 0; i < labels.length; i++) {
			newList.put(labels[i], selectedFetchers.get(labels[i]));
		}
		selectedFetchers = newList;
		
		// invorm observers
		for (Iterator i = updateListeners.iterator(); i.hasNext();) {
			FetcherRegistryUpdateListener listener = (FetcherRegistryUpdateListener) i.next();
			listener.handleUpdateOfSelectedFetchers(this);
		}
	}
	
}
