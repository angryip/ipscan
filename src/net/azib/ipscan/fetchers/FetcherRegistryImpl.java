/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.ArrayList;
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
	
	/**
	 * Private constructor
	 */
	public FetcherRegistryImpl() {
		fetchers = new ArrayList();
		fetchers.add(new IPFetcher());
		fetchers.add(new PingFetcher());
		fetchers.add(new PingTTLFetcher());
		fetchers.add(new HostnameFetcher());
		fetchers.add(new PortsFetcher());
		fetchers.add(new FilteredPortsFetcher());
		fetchers = Collections.unmodifiableList(fetchers);
	}

	/* (non-Javadoc)
	 * @see net.azib.ipscan.fetchers.FetcherRegistry#getRegisteredFetchers()
	 */
	public List getRegisteredFetchers() {
		return fetchers;
	}
	
	/* (non-Javadoc)
	 * @see net.azib.ipscan.fetchers.FetcherRegistry#getSelectedFetchers()
	 */
	public List getSelectedFetchers() {
		// TODO: support true selected fethers
		return fetchers;
	}

	/* (non-Javadoc)
	 * @see net.azib.ipscan.fetchers.FetcherRegistry#getSelectedFetcherIndex(java.lang.String)
	 */
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
