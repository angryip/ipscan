/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.PreferencesDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.SelectFetchersDialog;
import net.azib.ipscan.gui.StatisticsDialog;
import net.azib.ipscan.gui.StatusBar;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * ToolsActions
 * 
 * @author Anton Keks
 */
public class ToolsActions {

	public static final class Preferences implements Listener {
		
		private final PreferencesDialog preferencesDialog;
		private final ResultTable resultTable;
		private final StatusBar statusBar;
		
		public Preferences(PreferencesDialog preferencesDialog, ResultTable resultTable, StatusBar statusBar) {
			this.preferencesDialog = preferencesDialog;
			this.resultTable = resultTable;
			this.statusBar = statusBar;
		}

		public void handleEvent(Event event) {
			// show the preferences dialog
			preferencesDialog.open();
			
			// refresh the results and status bar in case anything was changed
			resultTable.updateResults();
			resultTable.updateColumnNames();
			statusBar.updateConfigText();
		}
	}

	public static final class ChooseFetchers implements Listener {
		
		private final SelectFetchersDialog selectFetchersDialog;
		
		public ChooseFetchers(SelectFetchersDialog selectFetchersDialog) {
			this.selectFetchersDialog = selectFetchersDialog;
		}

		public void handleEvent(Event event) {
			selectFetchersDialog.open();
		}

	}

	public static final class ScanInfo implements Listener, StateTransitionListener {
		
		private final StatisticsDialog statisticsDialog;
		private final GUIConfig guiConfig;
		
		public ScanInfo(GUIConfig guiConfig, StatisticsDialog statisticsDialog, StateMachine stateMachine) {
			this.guiConfig = guiConfig;
			this.statisticsDialog = statisticsDialog;
			// register for state changes
			stateMachine.addTransitionListener(this);
		}

		public void handleEvent(Event event) {
			statisticsDialog.open();
		}

		public void transitionTo(ScanningState state) {
			// switching to IDLE means the end of scanning
			if (state == ScanningState.IDLE && guiConfig.showScanStats) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						handleEvent(null);						
					}
				});				
			}
		}
	}
	
	/**
	 * This listener updates the status bar when user selects many items in the result table
	 */
	public static final class TableSelection implements Listener {
		private final StatusBar statusBar;
		private final StateMachine stateMachine;

		public TableSelection(StatusBar statusBar, StateMachine stateMachine) {
			this.statusBar = statusBar;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event event) {
			if (stateMachine.inState(ScanningState.IDLE)) {
				Table resultTable = (Table) event.widget;
				int selectionCount = resultTable.getSelectionCount();
				if (selectionCount > 1) 
					statusBar.setStatusText(selectionCount + Labels.getLabel("text.hostsSelected"));
				else
					statusBar.setStatusText(null);
			}
		}
	}
	
	static abstract class SelectDesired implements Listener {
		
		private final ResultTable resultTable;
		private final ScanningResultList results;
		private final TableSelection tableSelectionListener;

		public SelectDesired(ResultTable resultTable, ScanningResultList results, TableSelection tableSelectionListener) {
			this.resultTable = resultTable;
			this.results = results;
			this.tableSelectionListener = tableSelectionListener;
		}

		public void handleEvent(Event event) {
			int count = resultTable.getItemCount();
			resultTable.deselectAll();
			for (int i = 0; i < count; i++) {
				if (isDesired(results.getResult(i).getType())) {
					resultTable.select(i);
				}
			}
			event.widget = resultTable;
			tableSelectionListener.handleEvent(event);
			resultTable.forceFocus();
		}

		abstract boolean isDesired(ResultType type);
	}
	
	public static final class SelectAlive extends SelectDesired {
		public SelectAlive(ResultTable resultTable, ScanningResultList results, TableSelection tableSelectionListener) {
			super(resultTable, results, tableSelectionListener);
		}

		boolean isDesired(ResultType type) {
			return type.ordinal() >= ResultType.ALIVE.ordinal();
		}
	}

	public static final class SelectDead extends SelectDesired {
		public SelectDead(ResultTable resultTable, ScanningResultList results, TableSelection tableSelectionListener) {
			super(resultTable, results, tableSelectionListener);
		}

		boolean isDesired(ResultType type) {
			return type == ResultType.DEAD;
		}
	}
	
	public static final class SelectWithPorts extends SelectDesired {
		public SelectWithPorts(ResultTable resultTable, ScanningResultList results, TableSelection tableSelectionListener) {
			super(resultTable, results, tableSelectionListener);
		}

		boolean isDesired(ResultType type) {
			return type == ResultType.WITH_PORTS;
		}
	}
	
	public static final class SelectWithoutPorts extends SelectDesired {
		public SelectWithoutPorts(ResultTable resultTable, ScanningResultList results, TableSelection tableSelectionListener) {
			super(resultTable, results, tableSelectionListener);
		}

		boolean isDesired(ResultType type) {
			return type == ResultType.ALIVE;
		}
	}
}
