/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.state;

import static org.junit.Assert.*;

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
		stateMachine = new StateMachine();
	}
	
	@Test
	public void inState() throws Exception {
		assertTrue(stateMachine.inState(ScanningState.IDLE));
		assertFalse(stateMachine.inState(ScanningState.KILLING));
		stateMachine.transitionTo(ScanningState.KILLING);
		assertTrue(stateMachine.inState(ScanningState.KILLING));
	}
	
	@Test
	public void transitionTo() throws Exception {
		final boolean[] called = {false};
		ScanningState.IDLE.addTransitionListener(new StateTransitionListener() {
			public void transitionTo(ScanningState state) {
				fail("no transition if changing to the same state");
			}
		});
		ScanningState.STOPPING.addTransitionListener(new StateTransitionListener() {
			public void transitionTo(ScanningState state) {
				called[0] = true;
			}
		});
		
		assertEquals(ScanningState.IDLE, stateMachine.getState());
		stateMachine.transitionTo(ScanningState.IDLE);
		assertEquals(ScanningState.IDLE, stateMachine.getState());
		stateMachine.transitionTo(ScanningState.STOPPING);
		assertEquals(ScanningState.STOPPING, stateMachine.getState());
		assertTrue(called[0]);
		
		ScanningState.IDLE.clearListeners();
		ScanningState.STOPPING.clearListeners();
	}
	
	@Test
	public void transitionToNext() throws Exception {
		assertEquals(ScanningState.IDLE, stateMachine.getState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.SCANNING, stateMachine.getState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.STOPPING, stateMachine.getState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.KILLING, stateMachine.getState());
		stateMachine.transitionToNext();
		assertEquals(ScanningState.KILLING, stateMachine.getState());
	}
	
	@Test
	public void stop() throws Exception {
		stateMachine.transitionTo(ScanningState.SCANNING);
		stateMachine.stop();
		assertEquals(ScanningState.STOPPING, stateMachine.getState());
	}
	
	@Test
	public void complete() throws Exception {
		stateMachine.transitionTo(ScanningState.STOPPING);
		stateMachine.complete();
		assertEquals(ScanningState.IDLE, stateMachine.getState());
		
		stateMachine.transitionTo(ScanningState.KILLING);
		stateMachine.complete();
		assertEquals(ScanningState.IDLE, stateMachine.getState());
	}
}
