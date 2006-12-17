/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fetcher Registry singleton class.
 * Actually, it registers both plugins and builtins.
 *
 * @author anton
 */
public class FetcherRegistryImpl implements FetcherRegistry {
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private List fetchers;
	
	public FetcherRegistryImpl(Fetcher[] registeredFetchers) {
		fetchers = Arrays.asList(registeredFetchers);
		fetchers = Collections.unmodifiableList(fetchers);
	}

	public List getRegisteredFetchers() {
		return fetchers;
	}
	
	public List getSelectedFetchers() {
		// TODO: support true selected fethers
		return fetchers;
	}

	public int getSelectedFetcherIndex(String label) {
		// TODO: this probably needs to be changed to reflect selected fetchers and be more effective
		for (int i = 0; i < fetchers.size(); i++) {
			if (label.equals(((Fetcher)fetchers.get(i)).getLabel())) {
				return i;
			}
		}
		return -1;
	}
	
}
