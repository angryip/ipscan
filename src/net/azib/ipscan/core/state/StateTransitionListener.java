/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

import net.azib.ipscan.core.state.StateMachine.Transition;

/**
 * StateTransitionListener
 *
 * @author Anton Keks
 */
public interface StateTransitionListener {

	/**
	 * Notifies on transition to the specified state.
	 * @param state 
	 * @param transition 
	 */
	public void transitionTo(ScanningState state, Transition transition);

}
