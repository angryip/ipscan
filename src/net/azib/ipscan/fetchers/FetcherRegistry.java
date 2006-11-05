/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.List;

/**
 * FetcherRegistry
 *
 * @author anton
 */
public interface FetcherRegistry {

	/**
	 * @return a List of all registered Fetchers
	 */
	public abstract List getRegisteredFetchers();
	
	/**
	 * @return a List of selected Fetchers only
	 */
	public abstract List getSelectedFetchers();

	/**
	 * Searches for selected fetcher with the given label
	 * @param label
	 * @return the index, if found, or -1
	 */
	public abstract int getSelectedFetcherIndex(String label);

}
