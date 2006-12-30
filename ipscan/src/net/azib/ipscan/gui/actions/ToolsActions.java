/**
 * 
 */
package net.azib.ipscan.gui.actions;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import net.azib.ipscan.gui.OptionsDialog;
import net.azib.ipscan.gui.SelectFetchersDialog;

/**
 * ToolsActions
 * 
 * @author anton
 */
public class ToolsActions {

	public static class Options implements Listener {
		
		private OptionsDialog optionsWindow;
		
		public Options(OptionsDialog optionsWindow) {
			this.optionsWindow = optionsWindow;
		}

		public void handleEvent(Event event) {
			optionsWindow.open();
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
