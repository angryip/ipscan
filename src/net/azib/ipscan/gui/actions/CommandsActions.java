/**
 * 
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.gui.DetailsWindow;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Commands and Context menu Actions.
 * All these operate on the items, selected in the results list.
 *
 * @author anton
 */
public class CommandsActions {
	
	public static class Details implements Listener {
		ResultTable resultTable;
		
		public Details(ResultTable table) {
			resultTable = table;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			new DetailsWindow(resultTable); 
		}
	}
	
	/**
	 * Checks that there is at least one item selected in the results list.
	 * @param mainWindow
	 */
	private static void checkSelection(ResultTable resultTable) {
		if (resultTable.getItemCount() <= 0) {
			throw new UserErrorException("commands.noResults");
		}
		else
		if (resultTable.getSelectionIndex() < 0) {
			throw new UserErrorException("commands.noSelection");
		}
	}
}
