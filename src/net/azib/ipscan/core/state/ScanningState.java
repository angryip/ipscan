/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;


/**
 * ScanningState enum - all possible states.
 *
 * @author Anton Keks
 */
public enum ScanningState {
	
	IDLE,
	STARTING,
	SCANNING,
	STOPPING,
	KILLING,
	RESTARTING;
	
	/**
	 * Transitions the state to the next one.
	 * Note: not all states have the default next state;
	 */
	ScanningState next() {
		switch (this) {
			case IDLE: return STARTING;
			case STARTING: return SCANNING;
			case SCANNING: return STOPPING;
			case STOPPING: return KILLING;
			case RESTARTING: return SCANNING;
			default: return null;
		}
	}
}
