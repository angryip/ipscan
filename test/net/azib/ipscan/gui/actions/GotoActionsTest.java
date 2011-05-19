/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.actions.GotoMenuActions.NextHost;
import net.azib.ipscan.gui.actions.GotoMenuActions.PrevHost;

import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * GotoActionsTest
 *
 * @author Anton Keks
 */
public class GotoActionsTest {
    private ResultTable table = mock(ResultTable.class, RETURNS_DEEP_STUBS);
    private ScanningResultList results = table.getScanningResults();

    @Test
	public void nextHostFindsFirstHost() throws Exception {
        when(table.getItemCount()).thenReturn(2);
		when(table.getSelectionIndex()).thenReturn(-1);
        when(results.getResult(0).getType()).thenReturn(ResultType.WITH_PORTS);
        new NextHost(table, ResultType.ALIVE).handleEvent(null);
        verify(table).setFocus();
		verify(table).setSelection(0);
    }

    @Test
    public void nextHostStartsFromMiddleRewindsAndFindsFirstOne() {
		when(table.getItemCount()).thenReturn(2);
		when(table.getSelectionIndex()).thenReturn(0);
        when(results.getResult(1).getType()).thenReturn(ResultType.DEAD);
        when(results.getResult(0).getType()).thenReturn(ResultType.ALIVE);
        when(table.getSelectionIndex()).thenReturn(-1);
		new NextHost(table, ResultType.ALIVE).handleEvent(null);
        verify(table).setFocus();
        verify(table).setSelection(0);
    }

    @Test
    public void nextHostFindsSecondItem() {
		when(table.getItemCount()).thenReturn(2);
		when(table.getSelectionIndex()).thenReturn(-1);
        when(results.getResult(0).getType()).thenReturn(ResultType.WITH_PORTS);
        when(results.getResult(1).getType()).thenReturn(ResultType.DEAD);
        new NextHost(table, ResultType.DEAD).handleEvent(null);
        verify(table).setFocus();
        verify(table).setSelection(1);
	}

    @Test
	public void prevHostFindsLastHost() throws Exception {
        when(table.getItemCount()).thenReturn(10);
		when(table.getSelectionIndex()).thenReturn(-1);
        when(results.getResult(9).getType()).thenReturn(ResultType.WITH_PORTS);
        when(table.setFocus()).thenReturn(true);
		new PrevHost(table, ResultType.ALIVE).handleEvent(null);
        verify(table).setFocus();
        verify(table).setSelection(9);
    }

    @Test
    public void prevHostRewindsAndFindsLastItem() {
		when(table.getItemCount()).thenReturn(25);
		when(table.getSelectionIndex()).thenReturn(0);
        when(results.getResult(24).getType()).thenReturn(ResultType.DEAD);
        when(results.getResult(23).getType()).thenReturn(ResultType.ALIVE);
        when(table.getSelectionIndex()).thenReturn(-1);
		new PrevHost(table, ResultType.ALIVE).handleEvent(null);
        verify(table).setFocus();
        verify(table).setSelection(23);
    }

    @Test
    public void prevHostDoesntFindAnything() {
		when(table.getItemCount()).thenReturn(1);
		when(table.getSelectionIndex()).thenReturn(-1);
        when(results.getResult(0).getType()).thenReturn(ResultType.DEAD);
        new PrevHost(table, ResultType.ALIVE).handleEvent(null);
        verify(table, never()).setSelection(anyInt());
	}
}
