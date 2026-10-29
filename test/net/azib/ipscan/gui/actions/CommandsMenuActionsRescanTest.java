/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.actions;

import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.gui.ResultTable;
import org.eclipse.swt.widgets.Event;
import org.junit.Test;

import static net.azib.ipscan.core.state.ScanningState.*;
import static org.mockito.Mockito.*;

/**
 * CommandsMenuActionsRescanTest
 */
public class CommandsMenuActionsRescanTest {
	private final ResultTable resultTable = mock(ResultTable.class);
	private final StateMachine stateMachine = mock(StateMachine.class);
	private final StartStopScanningAction startStopScanningAction = mock(StartStopScanningAction.class);

	@Test
	public void startsRescanWhenIdle() {
		when(resultTable.getItemCount()).thenReturn(1);
		when(resultTable.getSelectionIndex()).thenReturn(0);
		when(stateMachine.inState(IDLE)).thenReturn(true);

		rescan().handleEvent(new Event());

		verify(stateMachine).rescan();
		verify(startStopScanningAction, never()).addSelectionToRescan();
	}

	@Test
	public void addsMoreIpsToRunningRescan() {
		when(resultTable.getItemCount()).thenReturn(1);
		when(resultTable.getSelectionIndex()).thenReturn(0);
		when(stateMachine.inState(IDLE)).thenReturn(false);
		when(stateMachine.inState(SCANNING)).thenReturn(true);

		rescan().handleEvent(new Event());

		verify(startStopScanningAction).addSelectionToRescan();
		verify(stateMachine, never()).rescan();
	}

	@Test
	public void resumesStoppingRescanWithMoreIps() {
		when(resultTable.getItemCount()).thenReturn(1);
		when(resultTable.getSelectionIndex()).thenReturn(0);
		when(stateMachine.inState(STOPPING)).thenReturn(true);
		when(startStopScanningAction.addSelectionToRescan()).thenReturn(true);
		when(stateMachine.resumeScanning()).thenReturn(true);

		rescan().handleEvent(new Event());

		verify(startStopScanningAction).addSelectionToRescan();
		verify(stateMachine).resumeScanning();
		verify(stateMachine, never()).rescan();
	}

	@Test
	public void startsNewRescanIfStoppingScanCompletedMeanwhile() {
		when(resultTable.getItemCount()).thenReturn(1);
		when(resultTable.getSelectionIndex()).thenReturn(0);
		when(stateMachine.inState(STOPPING)).thenReturn(true);
		when(startStopScanningAction.addSelectionToRescan()).thenReturn(true);
		when(stateMachine.resumeScanning()).thenReturn(false);
		when(stateMachine.inState(IDLE)).thenReturn(true);

		rescan().handleEvent(new Event());

		verify(stateMachine).rescan();
	}

	private CommandsMenuActions.Rescan rescan() {
		return new CommandsMenuActions.Rescan(resultTable, stateMachine, startStopScanningAction);
	}
}
