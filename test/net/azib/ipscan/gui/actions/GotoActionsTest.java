/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.actions.GotoActions.NextHost;
import net.azib.ipscan.gui.actions.GotoActions.PrevHost;

import org.junit.Test;

import static org.easymock.classextension.EasyMock.*;

/**
 * GotoActionsTest
 *
 * @author Anton Keks
 */
public class GotoActionsTest {

	@Test
	public void nextHost() throws Exception {
		ResultTable table = createMock(ResultTable.class);
		ScanningResultList results = createMock(ScanningResultList.class);
		NextHost nextHostAction = new NextHost(table, ResultType.ALIVE);
		
		// first host (0) is found
		expect(table.getScanningResults()).andReturn(results);
		expect(table.getItemCount()).andReturn(2);
		expect(table.getSelectionIndex()).andReturn(-1);
		expect(results.getResult(0)).andReturn(result(ResultType.WITH_PORTS));
		table.setSelection(0); expectLastCall();
		expect(table.setFocus()).andReturn(true);
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);

		// start from the middle, rewind, first is found
		reset(table, results);
		expect(table.getScanningResults()).andReturn(results).times(2);
		expect(table.getItemCount()).andReturn(2).times(2);
		expect(table.getSelectionIndex()).andReturn(0);
		expect(results.getResult(1)).andReturn(result(ResultType.DEAD));
		expect(results.getResult(0)).andReturn(result(ResultType.ALIVE));
		table.deselectAll(); expectLastCall();
		expect(table.getSelectionIndex()).andReturn(-1);
		table.setSelection(0); expectLastCall();
		expect(table.setFocus()).andReturn(true);
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);
		
		// second dead host (1) is found
		nextHostAction =  new NextHost(table, ResultType.DEAD);
		reset(table, results);
		expect(table.getScanningResults()).andReturn(results);
		expect(table.getItemCount()).andReturn(2);
		expect(table.getSelectionIndex()).andReturn(-1);
		expect(results.getResult(0)).andReturn(result(ResultType.WITH_PORTS));
		expect(results.getResult(1)).andReturn(result(ResultType.DEAD));
		table.setSelection(1); expectLastCall();
		expect(table.setFocus()).andReturn(true);
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);
	}
	
	@Test
	public void prevHost() throws Exception {
		ResultTable table = createMock(ResultTable.class);
		ScanningResultList results = createMock(ScanningResultList.class);
		PrevHost nextHostAction = new PrevHost(table, ResultType.ALIVE);
		
		// last host (9) is found
		expect(table.getScanningResults()).andReturn(results);
		expect(table.getItemCount()).andReturn(10).times(2);
		expect(table.getSelectionIndex()).andReturn(-1);
		expect(results.getResult(9)).andReturn(result(ResultType.WITH_PORTS));
		table.setSelection(9); expectLastCall();
		expect(table.setFocus()).andReturn(true);
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);

		// start from the start, rewind to the end, last is found
		reset(table, results);
		expect(table.getScanningResults()).andReturn(results).times(2);
		expect(table.getItemCount()).andReturn(25).times(3);
		expect(table.getSelectionIndex()).andReturn(0);
		expect(results.getResult(24)).andReturn(result(ResultType.DEAD));
		expect(results.getResult(23)).andReturn(result(ResultType.ALIVE));
		table.deselectAll(); expectLastCall();
		expect(table.getSelectionIndex()).andReturn(-1);
		table.setSelection(23); expectLastCall();
		expect(table.setFocus()).andReturn(true);
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);
		
		// not found
		reset(table, results);
		expect(table.getScanningResults()).andReturn(results);
		expect(table.getItemCount()).andReturn(1).times(2);
		expect(table.getSelectionIndex()).andReturn(-1);
		expect(results.getResult(0)).andReturn(result(ResultType.DEAD));
		replay(table, results);		
		nextHostAction.handleEvent(null);
		verify(table, results);
	}

	private ScanningResult result(ResultType type) {
		ScanningResult result = createMock(ScanningResult.class);
		expect(result.getType()).andReturn(type);
		replay(result);
		return result;
	}
}
