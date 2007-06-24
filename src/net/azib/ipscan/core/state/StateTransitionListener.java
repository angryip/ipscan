/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

/**
 * StateTransitionListener
 *
 * @author Anton Keks
 */
public interface StateTransitionListener {

	/**
	 * Notifies on transition to the specified state.
	 * @param state 
	 */
	public void transitionTo(ScanningState state);

}
