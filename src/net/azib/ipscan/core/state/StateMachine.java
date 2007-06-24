/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

import java.util.logging.Logger;

import net.azib.ipscan.config.LoggerFactory;


/**
 * StateMachine
 *
 * @author Anton Keks
 */
public class StateMachine {
	
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScanningState state = ScanningState.IDLE;
	
	/**
	 * @param state
	 * @return true if current state is as specified
	 */
	public boolean inState(ScanningState state) {
		return this.state == state;
	}
	
	/**
	 * @return current state
	 */
	public ScanningState getState() {
		return state;
	}

	/**
	 * Transitions to the specified state, notifying all listeners.
	 * @param newState
	 */
	public void transitionTo(ScanningState newState) {
		state = newState;
		state.notifyOnEntry();
	}

	/**
	 * Transitions to the next state in the sequence.
	 * Called when user presses the scan button.
	 */
	public void transitionToNext() {
		// killing state cannot be transitioned from by pressing a button
		if (state != ScanningState.KILLING) {
			transitionTo(state.next());
		}
	}

	/**
	 * Transitions to the stopping state
	 */
	public void stop() {
		if (state == ScanningState.SCANNING) {
			transitionTo(ScanningState.STOPPING);
		}
		else {
			LOG.warning("Attempt to stop from " + state);
		}
	}

	/**
	 * Transitions back to the idle state
	 */
	public void complete() {
		if (state == ScanningState.STOPPING || state == ScanningState.KILLING) {
			transitionTo(ScanningState.IDLE);
		}		
		else {
			LOG.warning("Attempt to complete from " + state);
		}
	}

}
