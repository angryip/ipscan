/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

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
 * @author anton
 */
public interface Fetcher extends Cloneable {

	/**
	 * @return label ID, representing the name of this fetcher
	 */
	public String getLabel();
	
	/**
	 * Does the actual fetching.
	 * @param subject the scanning subject, containing an IP address
	 * @return String representation of the fetched data, null in case of any error.
	 */
	public String scan(ScanningSubject subject);
}
