/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import net.azib.ipscan.gui.OptionsDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.SelectFetchersDialog;
import net.azib.ipscan.gui.StatisticsDialog;
import net.azib.ipscan.gui.StatusBar;

/**
 * ToolsActions
 * 
 * @author anton
 */
public class ToolsActions {

	public static class Options implements Listener {
		
		private OptionsDialog optionsDialog;
		private ResultTable resultTable;
		private StatusBar statusBar;
		
		public Options(OptionsDialog optionsDialog, ResultTable resultTable, StatusBar statusBar) {
			this.optionsDialog = optionsDialog;
			this.resultTable = resultTable;
			this.statusBar = statusBar;
		}

		public void handleEvent(Event event) {
			// show the options dialog
			optionsDialog.open();
			
			// refresh the results and status bar in case anything was changed
			resultTable.updateResults();
			statusBar.updateConfigText();
		}
	}

	public static class SelectFetchers implements Listener {
		
		private SelectFetchersDialog selectFetchersDialog;
		
		public SelectFetchers(SelectFetchersDialog selectFetchersDialog) {
			this.selectFetchersDialog = selectFetchersDialog;
		}

		public void handleEvent(Event event) {
			selectFetchersDialog.open();
		}

	}

	public static class LastInfo implements Listener {
		
		private StatisticsDialog statisticsDialog;
		
		public LastInfo(StatisticsDialog statisticsDialog) {
			this.statisticsDialog = statisticsDialog;
		}

		public void handleEvent(Event event) {
			statisticsDialog.open();
		}
		
	}
}
