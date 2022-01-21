/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.Plugin;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.feeders.Feeder;

/**
 * Interface of all IP Fetchers.
 * 
 * Fetcher is responsible for gathering a certain type of 
 * information about the provided scanning subject 
 * (in GUI terms, Fetcher is a column in the results list). 
 * 
 * Fetchers do the actual information fetching about each 
 * scanned IP address.
 * 
 * Instances of this classes are shared among all the threads,
 * so implementations must be thread safe and stateless.
 * 
 * @author Anton Keks
 */
public interface Fetcher extends Cloneable, Plugin {

	/**
	 * @return full name to be displayed in the result table column. 
	 * It may contain a suffix useful to inform users about the fetcher's preferences.
	 */
	String getFullName();
	
	/**
	 * @return localized help text about the fetcher
	 */
	String getInfo();
	
	/**
	 * @return the preferences class that may be used for editing of this fetcher's preferences
	 * or null if no preferences editing is possible
	 */
	Class<? extends FetcherPrefs> getPreferencesClass();
	
	/**
	 * Does the actual fetching.
	 * @param subject the scanning subject, containing an IP address
	 * @return the fetched data (a String in most cases), null in case of any error. 
	 * Special values may also be returned, such as {@link NotAvailable} or {@link NotScanned}
	 */
	Object scan(ScanningSubject subject);

	/**
	 * Called before scanning has started to do any initialization stuff
	 */
	default void init(Feeder feeder) {
		init();
	}

	default void init() {}

	/**
	 * Called after the scanning has been completed to do any cleanup needed
	 */
	void cleanup();
}
