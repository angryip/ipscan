/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

import java.util.HashSet;
import java.util.Set;

/**
 * ScanningState enum - all possible states.
 *
 * @author Anton Keks
 */
public enum ScanningState {
	
	IDLE,
	SCANNING,
	STOPPING,
	KILLING;
	
	private Set<StateTransitionListener> listeners = new HashSet<StateTransitionListener>();
	
	/**
	 * Transitions the state to the next one
	 */
	public ScanningState next() {
		ScanningState[] states = values();
		return states[ordinal()+1 % states.length];
	}
	
	/**
	 * Notifies all registered listeners of the transition to this state.
	 */
	public void notifyOnEntry() {
		for (StateTransitionListener listener : listeners) {
			listener.transitionTo(this);
		}
	}

}
