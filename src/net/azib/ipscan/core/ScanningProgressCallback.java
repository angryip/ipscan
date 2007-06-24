/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

import net.azib.ipscan.core.state.ScanningState;

/**
 * This callback is called on scanning state updates.
 * 
 * @author anton
 */
public interface ScanningProgressCallback {
		
	/**
	 * This method is called on scanner state changes,
	 * eg. when scanning is about to stop.
	 * 
	 * @param new state
	 */
	public void scannerStateChanged(ScanningState state);
	
	/**
	 * This method is called on scanning progress updates.
	 * There are no guarantees that this method is called on every
	 * scanning iteration. 
	 * 
	 * @param currentAddress currently scanned IP address, can be null
	 * @param runningThreads number of currently running threads
	 * @param percentageComplete value from 0 to 100, showing how much work
	 * 		is already done.
	 */
	public void updateProgress(InetAddress currentAddress, int runningThreads, int percentageComplete);
}
