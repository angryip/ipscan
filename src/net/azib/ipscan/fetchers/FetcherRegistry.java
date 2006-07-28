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
public class FetcherRegistry {
	
	private static FetcherRegistry instance;
	
	/** All available Fetcher implementations, List of Fetcher instances */
	private List fetchers;
	
	static {
		// TODO: maybe it is better to call it from the main class?
		initialize();
	}
	
	public static FetcherRegistry getInstance() {
		return instance;
	}
	
	public static void initialize() {
		instance = new FetcherRegistry();
	}
	
	/**
	 * Private constructor
	 */
	private FetcherRegistry() {
		fetchers = new ArrayList();
		fetchers.add(new IPFetcher());
		fetchers.add(new PingFetcher());
		fetchers.add(new PingTTLFetcher());
		fetchers.add(new HostnameFetcher());
		fetchers.add(new PortsFetcher());
		fetchers.add(new FilteredPortsFetcher());
		fetchers = Collections.unmodifiableList(fetchers);
	}

	/**
	 * @return a List of all registered Fetchers
	 */
	public List getRegisteredFetchers() {
		return fetchers;
	}
	
}
