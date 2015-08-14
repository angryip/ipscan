/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.CommandLineProcessor;
import net.azib.ipscan.config.CommandProcessor;
import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import javax.inject.Inject;

import static net.azib.ipscan.core.ScanningResult.ResultType.*;

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
		
		@Inject public Preferences(PreferencesDialog preferencesDialog, ResultTable resultTable, StatusBar statusBar) {
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
		
		@Inject public ChooseFetchers(SelectFetchersDialog selectFetchersDialog) {
			this.selectFetchersDialog = selectFetchersDialog;
		}

		public void handleEvent(Event event) {
			selectFetchersDialog.open();
		}

	}

	public static final class ScanStatistics implements Listener, StateTransitionListener {
		
		private final StatisticsDialog statisticsDialog;
		private final GUIConfig guiConfig;

		@Inject
		public ScanStatistics(GUIConfig guiConfig, StatisticsDialog statisticsDialog, StateMachine stateMachine, CommandLineProcessor commandProcessor) {
			this.guiConfig = guiConfig;
			this.statisticsDialog = statisticsDialog;
			// register for state changes
			if (!commandProcessor.shouldAutoQuit())
				stateMachine.addTransitionListener(this);
		}

		public void handleEvent(Event event) {
			statisticsDialog.open();
		}

		public void transitionTo(ScanningState state, Transition transition) {
			// switching to IDLE means the end of scanning
			if (transition == Transition.COMPLETE && guiConfig.showScanStats) {
				handleEvent(null);						
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

		public SelectDesired(ResultTable resultTable) {
			this.resultTable = resultTable;
			this.results = resultTable.getScanningResults();
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
			resultTable.notifyListeners(SWT.Selection, event);
			resultTable.forceFocus();
		}

		abstract boolean isDesired(ResultType type);
	}
	
	public static final class SelectAlive extends SelectDesired {
		@Inject
		public SelectAlive(ResultTable resultTable) {
			super(resultTable);
		}

		boolean isDesired(ResultType type) {
			return type.ordinal() >= ALIVE.ordinal();
		}
	}

	public static final class SelectDead extends SelectDesired {
		@Inject
		public SelectDead(ResultTable resultTable) {
			super(resultTable);
		}

		boolean isDesired(ResultType type) {
			return type == DEAD;
		}
	}
	
	public static final class SelectWithPorts extends SelectDesired {
		@Inject
		public SelectWithPorts(ResultTable resultTable) {
			super(resultTable);
		}

		boolean isDesired(ResultType type) {
			return type == WITH_PORTS;
		}
	}
	
	public static final class SelectWithoutPorts extends SelectDesired {
		@Inject
		public SelectWithoutPorts(ResultTable resultTable) {
			super(resultTable);
		}

		boolean isDesired(ResultType type) {
			return type == ALIVE;
		}
	}
	
	/** 
	 * This cannot be accessed from the menu, but it provides the Ctrl+A
	 * Select All functionality for Windows (other platforms implement this themselves)
	 */
	public static class SelectAll implements Listener {
		private final ResultTable resultTable;

		public SelectAll(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			// Ctrl+A handler
			if (event.type == SWT.KeyDown && event.keyCode == 'a' && event.stateMask == SWT.MOD1) {
				resultTable.selectAll();
				// update selection status
				event.widget = resultTable;
        		resultTable.notifyListeners(SWT.Selection, event);
				event.doit = false;
			}
		}
	}

	public static final class SelectInvert implements Listener {
		private final ResultTable resultTable;

		@Inject
		public SelectInvert(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			int count = resultTable.getItemCount();
			// the most naive implementation
			resultTable.setRedraw(false);
			for (int i = 0; i < count; i++) {
				if (resultTable.isSelected(i)) 
					resultTable.deselect(i);
				else
					resultTable.select(i);
			}
			resultTable.setRedraw(true);
			resultTable.redraw();
			event.widget = resultTable;
      resultTable.notifyListeners(SWT.Selection, event);
			resultTable.forceFocus();
		}
	}
}
