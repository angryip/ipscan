/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.state;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Generic StateMachine implementation.
 * It holds the current state and performs transitions with corresponding methods.
 * <p/>
 * Note: the class is abstract because notification of listeners often should happen in the correct thread,
 * so subclasses should provide this functionality.
 *
 * @author Anton Keks
 */
public abstract class StateMachine {
	
	public enum Transition {INIT, START, STOP, NEXT, COMPLETE, RESET, RESCAN}
	
	private volatile ScanningState state = ScanningState.IDLE;
	
	private ReentrantReadWriteLock listenersLock = new ReentrantReadWriteLock();
	private List<StateTransitionListener> transitionListeners = new ArrayList<StateTransitionListener>();
	
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
		try {
			listenersLock.writeLock().lock();
			transitionListeners.add(listener);
		}
		finally {
			listenersLock.writeLock().unlock();
		}
	}
	
	/**
	 * Unregisters the listener
	 * @param killHandler
	 */
	public void removeTransitionListener(StateTransitionListener listener) {
		try {
			listenersLock.writeLock().lock();
			transitionListeners.remove(listener);
		}
		finally {
			listenersLock.writeLock().unlock();
		}
	}

	/**
	 * Transitions to the specified state, notifying all listeners.
	 * Note: this method is intentionally not public, use specific methods to make desired transitions.
	 * @param newState
	 */
	void transitionTo(ScanningState newState, Transition transition) {
		if (state != newState) {
			state = newState;
			notifyAboutTransition(transition);
		}
	}

	protected void notifyAboutTransition(Transition transition) {		
		try {
			listenersLock.readLock().lock();
			for (StateTransitionListener listener : transitionListeners) {
				listener.transitionTo(state, transition);
			}			
		}
		finally {
			listenersLock.readLock().unlock();
		}
	}

	/**
	 * Transitions to the next state in the sequence.
	 * Called when user presses the scan button.
	 */
	public void transitionToNext() {
		// killing state cannot be transitioned from by pressing a button
		if (state != ScanningState.KILLING) {
			transitionTo(state.next(), Transition.NEXT);
		}
	}

	/**
	 * Transitions to the stopping state
	 */
	public void stop() {
		if (state == ScanningState.SCANNING) {
			transitionTo(ScanningState.STOPPING, Transition.STOP);
		}
		else if (state == ScanningState.STOPPING) {
			// notify anyway to ensure that manual stopping and automatic stopping work well together
			notifyAboutTransition(Transition.STOP);
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
			transitionTo(ScanningState.IDLE, Transition.COMPLETE);
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
			transitionTo(ScanningState.RESTARTING, Transition.RESCAN);
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
			transitionTo(ScanningState.SCANNING, Transition.START);
		}
		else {
			throw new IllegalStateException("Attempt to go scanning from " + state);
		}
	}

	/**
	 * Inits everyone on startup
	 */
	public void init() {
		state = ScanningState.IDLE;
		notifyAboutTransition(Transition.INIT);
	}

	/**
	 * Resets the machine to the initial state
	 */
	public void reset() {
		// no transition notifications
		state = ScanningState.IDLE;
	}

}
