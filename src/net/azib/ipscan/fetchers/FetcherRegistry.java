/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import java.util.Collection;

/**
 * FetcherRegistry
 *
 * @author Anton Keks
 */
public interface FetcherRegistry {

	/**
	 * @return a List of all registered Fetchers
	 */
	public Collection<Fetcher> getRegisteredFetchers();
	
	/**
	 * @return a List of selected Fetchers only
	 */
	public Collection<Fetcher> getSelectedFetchers();

	/**
	 * Searches for selected fetcher with the given label
	 * @param id
	 * @return the index, if found, or -1
	 */
	public int getSelectedFetcherIndex(String id);

	/**
	 * Updates the list, retaining only items that are passed in the array.
	 * The order of elements will be the same as in the array.
	 * 
	 * @param names
	 */
	public void updateSelectedFetchers(String[] names);

	/**
	 * Adds a listener to observe FetcherRegistry events, like modification of selected fetchers.
	 * @param listener
	 */
	public void addListener(FetcherRegistryUpdateListener listener);

	/**
	 * Opens preferences editor for the specified fetcher, if possible.
	 * @param fetcher
	 * @throws FetcherException if preferences editor doesn't exist
	 */
	public void openPreferencesEditor(Fetcher fetcher) throws FetcherException;

}
