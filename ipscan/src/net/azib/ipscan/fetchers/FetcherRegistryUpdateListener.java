/**
 * 
 */
package net.azib.ipscan.fetchers;

/**
 * FetcherRegistryUpdateListener.
 * Implement this interface if you need to react to FetcherRegistry updates.
 *
 * @author Anton Keks
 */
public interface FetcherRegistryUpdateListener {
	
	/**
	 * This method is called when the list of selected Fetchers was changed. 
	 * @param fetcherRegistry
	 */
	void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry);

}
