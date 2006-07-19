/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

/**
 * This callback is called to consume scanning results.
 *  
 * @author anton
 */
public interface ScanningResultsCallback {

	/**
	 * This method is called just before starting to retrieve
	 * scanning results for the specified address.
	 * @param address
	 * @return the method should return an int
	 */
	public int prepareForResults(InetAddress address);
	
	/**
	 * This method is called when scanning results are ready.
	 * @param preparationNumber the number, which was previously returned by prepareForResults().
	 * @param results the List of Strings, each String corresponds to a Fetcher
	 */
	public void consumeResults(int preparationNumber, ScanningResult results);
	
}
