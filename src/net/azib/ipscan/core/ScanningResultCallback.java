/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

/**
 * This callback is called to consume scanning results.
 *  
 * @author Anton Keks
 */
public interface ScanningResultCallback {

	/**
	 * This method is called just before starting to retrieve
	 * scanning results for the specified address.
	 * @param result empty results holder for a single address
	 * @return the method should return an int
	 */
	public void prepareForResults(ScanningResult result);
	
	/**
	 * This method is called when scanning results are ready.
	 * @param results filled results holder for a single address
	 */
	public void consumeResults(ScanningResult results);
	
}
