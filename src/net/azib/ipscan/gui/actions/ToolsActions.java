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

/**
 * ToolsActions
 * 
 * @author anton
 */
public class ToolsActions {

	public static class Options implements Listener {
		
		private OptionsDialog optionsDialog;
		private ResultTable resultTable;
		
		public Options(OptionsDialog optionsDialog, ResultTable resultTable) {
			this.optionsDialog = optionsDialog;
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			// show the options dialog
			optionsDialog.open();
			
			// refresh the results in case anything was changed
			resultTable.updateResults();
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
}
