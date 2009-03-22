/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import org.eclipse.swt.widgets.Display;

import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;

/**
 * Extends the generic {@link StateMachine} in order to run state transition notifications
 * in the SWT user-interface thread. This will allow {@link StateTransitionListener}s to call SWT methods without
 * the bloat of using the {@link Display#asyncExec(Runnable)} themselves.
 *
 * @author Anton Keks
 */
public class SWTAwareStateMachine extends StateMachine {
	
	private Display display;
	
	public SWTAwareStateMachine(Display display) {
		this.display = display;
	}

	@Override
	protected void notifyAboutTransition(final Transition transition) {
		if (display.isDisposed())
			return;

		// call super asynchronously in the correct thread
		display.asyncExec(new Runnable() {
			public void run() {
				SWTAwareStateMachine.super.notifyAboutTransition(transition);				
			}
		});
	}
	
}
