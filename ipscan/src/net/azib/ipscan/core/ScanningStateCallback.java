/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;

/**
 * This callback is called on scanning status updates.
 * 
 * @author anton
 */
public interface ScanningStateCallback {
	
	public static final int STATE_IDLE = 0;
	public static final int STATE_SCANNING = 1;
	public static final int STATE_STOPPING = 2;
	public static final int STATE_KILLING = 3;
	
	/**
	 * This method is called on scanner status changes,
	 * eg. when scanning is about to stop.
	 * 
	 * @param status integer value of current status, having the 
	 * 		corresponding STATE_XXX constant
	 */
	public void scannerStateChanged(int status);
	
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
