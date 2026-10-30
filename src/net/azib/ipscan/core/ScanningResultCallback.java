/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
	 */
	void prepareForResults(ScanningResult result);

	/**
	 * This method is called after each fetcher has completed for the specified address.
	 * It allows displaying intermediate results before the whole address is scanned.
	 * @param result partially filled results holder for a single address
	 */
	default void fetcherCompleted(ScanningResult result) {
	}
	
	/**
	 * This method is called when scanning results are ready.
	 * @param results filled results holder for a single address
	 */
	void consumeResults(ScanningResult results);
}
