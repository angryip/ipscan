/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

import net.azib.ipscan.core.ScanningProgressCallback;

/**
 * StateMachine
 *
 * @author Anton Keks
 */
public class StateMachine {
	
	private ScanningProgressCallback progressCallback;
	private ScanningState state = ScanningState.IDLE;
	
	public void transitionTo(ScanningState newState) {
		this.state = newState;
		progressCallback.scannerStateChanged(newState);
	}

	/**
	 * @param state
	 * @return true if current state is as specified
	 */
	public boolean isState(ScanningState state) {
		return this.state == state;
	}
	
	/**
	 * @return current state
	 */
	public ScanningState getState() {
		return state;
	}

	public void setScanningProgressCallback(ScanningProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}
	
}
