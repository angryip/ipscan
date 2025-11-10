/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
		return switch (this) {
			case IDLE -> STARTING;
			case STARTING -> SCANNING;
			case SCANNING -> STOPPING;
			case STOPPING -> KILLING;
			case RESTARTING -> SCANNING;
			default -> null;
		};
	}
}
