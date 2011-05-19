/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

/**
 * FetcherPrefs - an interface to implement for Fetcher preferences editor classes.
 *
 * @author Anton Keks
 */
public interface FetcherPrefs {
	/**
	 * Opens a self-maintained GUI editor of concrete fetcher preferences.
	 * @param fetcher to edit
	 */
	public void openFor(Fetcher fetcher);
}
