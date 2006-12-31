/**
 * 
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.MainMenu.ColumnsMenu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * ColumnsActions
 *
 * @author anton
 */
public class ColumnsActions {
	
	public static class ColumnResize implements Listener {
		public void handleEvent(Event event) {
			TableColumn column = (TableColumn) event.widget;
			// do not save the width of the last column on Linux, because in GTK 
			// it is stretched to the width of the whole table and therefore is incorrect
			if (Platform.LINUX && column.getParent().getColumn(column.getParent().getColumnCount()-1) == column) 
				return;

			// save column width
			Config.getDimensionsConfig().setColumnWidth(column.getText(), column.getWidth());
		}
	}

	public static class ColumnClick implements Listener {
		
		private Menu columnsMenu;
		
		public ColumnClick(ColumnsMenu columnsMenu) {
			this.columnsMenu = columnsMenu;
		}

		public void handleEvent(Event e) {
			// modify menu text a bit
			TableColumn tableColumn = (TableColumn) e.widget;
			MenuItem sortMenuItem = columnsMenu.getItem(0);
			if (tableColumn.getParent().getSortColumn() == tableColumn) {
				sortMenuItem.setText(Labels.getLabel("menu.columns.sortDirection"));
			}
			else {
				sortMenuItem.setText(Labels.getLabel("menu.columns.sortBy") + tableColumn.getText());
			}
			
			// remember the clicked column (see SortBy below)
			sortMenuItem.setData(tableColumn);
			
			// show the menu
			columnsMenu.setLocation(e.display.getCursorLocation());
			columnsMenu.setVisible(true);
		}
	}

	public static class SortBy implements Listener {
		
		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			TableColumn tableColumn = (TableColumn) event.widget.getData();
			
			Table table = tableColumn.getParent();
			
			if (table.getSortColumn() != tableColumn) {
				table.setSortColumn(tableColumn);
				table.setSortDirection(SWT.UP);
			} 
			else {
				table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
			}
			
			// TODO: execute ScanningResultList.sort() here!!!
		}

	}

}
