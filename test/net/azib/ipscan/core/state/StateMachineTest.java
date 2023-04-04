/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.state;

import static org.junit.Assert.*;

import net.azib.ipscan.core.state.StateMachine.Transition;

import org.junit.Before;
import org.junit.Test;

/**
 * StateMachineTest
 *
 * @author Anton Keks
 */
public class StateMachineTest {
	
	private StateMachine stateMachine;
	
	@Before
	public void createStateMachine() {
		// create empty subclass of StateMachine
		stateMachine = new StateMachine(){};
	}
	
	@Test
	public void inState() throws Exception {
		assertTrue(stateMachine.inCurrentState(ScanningState.IDLE));
		assertFalse(stateMachine.inCurrentState(ScanningState.KILLING));
		stateMachine.transitionTo(ScanningState.KILLING, null);
		assertTrue(stateMachine.inCurrentState(ScanningState.KILLING));
	}
	
	@Test
	public void transitionToSameState() throws Exception {
		stateMachine.addTransitionListener(new StateTransitionListener() {
			public void transitionTo(ScanningState state, Transition transition) {
				fail("no transition if changing to the same state");
			}
		});
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
		stateMachine.transitionTo(ScanningState.IDLE, null);
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
	}

	@Test
	public void transitionToAnotherState() throws Exception {
		final ScanningState[] calledWithParameter = {null};
		stateMachine.addTransitionListener(new StateTransitionListener() {
			public void transitionTo(ScanningState state, Transition transition) {
				calledWithParameter[0] = state;
			}
		});
		
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
		stateMachine.transitionTo(ScanningState.STOPPING, null);
		assertEquals(ScanningState.STOPPING, stateMachine.getCurrentState());
		assertEquals(ScanningState.STOPPING, calledWithParameter[0]);
		stateMachine.transitionTo(ScanningState.STARTING, null);
		assertEquals(ScanningState.STARTING, stateMachine.getCurrentState());
		assertEquals(ScanningState.STARTING, calledWithParameter[0]);
	}
	
	@Test
	public void transitionToNext() throws Exception {
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.STARTING, stateMachine.getCurrentState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.SCANNING, stateMachine.getCurrentState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.STOPPING, stateMachine.getCurrentState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.KILLING, stateMachine.getCurrentState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.KILLING, stateMachine.getCurrentState());
	}
	
	@Test
	public void stop() throws Exception {
		final int notificationCount[] = {0};
		stateMachine.addTransitionListener(new StateTransitionListener() {
			public void transitionTo(ScanningState state, Transition transition) {
				notificationCount[0]++;
			}
		});
		stateMachine.transitionTo(ScanningState.SCANNING, null);
		assertEquals(1, notificationCount[0]);
		stateMachine.stop();
		assertEquals(ScanningState.STOPPING, stateMachine.getCurrentState());
		assertEquals(2, notificationCount[0]);
		stateMachine.stop();
		assertEquals(ScanningState.STOPPING, stateMachine.getCurrentState());
		assertEquals(3, notificationCount[0]);
	}
	
	@Test
	public void complete() throws Exception {
		stateMachine.transitionTo(ScanningState.STOPPING, null);
		stateMachine.complete();
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
		
		stateMachine.transitionTo(ScanningState.KILLING, null);
		stateMachine.complete();
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
	}

	@Test
	public void rescan() throws Exception {
		stateMachine.transitionTo(ScanningState.IDLE, null);
		stateMachine.rescan();
		assertEquals(ScanningState.RESTARTING, stateMachine.getCurrentState());
	}

	@Test
	public void startScanning() throws Exception {
		stateMachine.transitionTo(ScanningState.STARTING, null);
		stateMachine.startScanning();
		assertEquals(ScanningState.SCANNING, stateMachine.getCurrentState());
		
		stateMachine.transitionTo(ScanningState.RESTARTING, null);
		stateMachine.startScanning();
		assertEquals(ScanningState.SCANNING, stateMachine.getCurrentState());
	}
	
	@Test
	public void reset() throws Exception {
		stateMachine.transitionTo(ScanningState.STARTING, null);
		stateMachine.reset();
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
		
		stateMachine.transitionTo(ScanningState.KILLING, null);
		stateMachine.reset();
		assertEquals(ScanningState.IDLE, stateMachine.getCurrentState());
	}
}
