/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StateMachine implementation.
 * It holds the current state and performs transitions with corresponding methods.
 *
 * @author Anton Keks
 */
public class StateMachine {
	
	private ScanningState state = ScanningState.IDLE;
	
	private List<StateTransitionListener> transitionListeners = Collections.synchronizedList(new ArrayList<StateTransitionListener>());
	
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
	 * Registers state transition listener.
	 * @param listener instance
	 */
	public void addTransitionListener(StateTransitionListener listener) {
		transitionListeners.add(listener);
	}
	
	/**
	 * Unregisters the listener
	 * @param killHandler
	 */
	public void removeTransitionListener(StateTransitionListener listener) {
		transitionListeners.remove(listener);
	}

	/**
	 * Transitions to the specified state, notifying all listeners.
	 * Note: this method is intentionally not public, use specific methods to make desired transitions.
	 * @param newState
	 */
	void transitionTo(ScanningState newState) {
		if (state != newState) {
			state = newState;
			notifyAboutTransition();
		}
	}

	private void notifyAboutTransition() {
		synchronized (transitionListeners) {
			for (StateTransitionListener listener : transitionListeners) {
				listener.transitionTo(state);
			}			
		}
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
		else if (state == ScanningState.STOPPING) {
			// notify anyway to ensure that manual stopping and automatic stopping work well together
			notifyAboutTransition();
		}
		else {
			throw new IllegalStateException("Attempt to stop from " + state);
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
			throw new IllegalStateException("Attempt to complete from " + state);
		}
	}

	/**
	 * Transitions to the RESTARTING state in order to rescan previously scanned results.
	 */
	public void rescan() {
		if (state == ScanningState.IDLE) {
			transitionTo(ScanningState.RESTARTING);
		}
		else {
			throw new IllegalStateException("Attempt to rescan from " + state);
		}
	}

	/**
	 * Starts the scanning process
	 */
	public void startScanning() {
		if (state == ScanningState.STARTING || state == ScanningState.RESTARTING) {
			transitionTo(ScanningState.SCANNING);
		}
		else {
			throw new IllegalStateException("Attempt to go scanning from " + state);
		}
	}

	/**
	 * Resets the machine to the initial state
	 */
	public void reset() {
		// no transition notifications
		state = ScanningState.IDLE;
	}

}
