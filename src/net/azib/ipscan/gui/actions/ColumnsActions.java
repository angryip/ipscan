/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.menu.ColumnsMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;

/**
 * ColumnsActions
 *
 * @author Anton Keks
 */
public class ColumnsActions {
	
	public static final class ColumnResize implements Listener {
		private GUIConfig guiConfig;
		
		@Inject public ColumnResize(GUIConfig guiConfig) {
			this.guiConfig = guiConfig;
		}

		public void handleEvent(Event event) {
			TableColumn column = (TableColumn) event.widget;
			// do not save the width of the last column on Linux, because in GTK 
			// it is stretched to the width of the whole table and therefore is incorrect
			if (Platform.LINUX && column.getParent().getColumn(column.getParent().getColumnCount()-1) == column) 
				return;

			// save column width
			guiConfig.setColumnWidth((Fetcher)column.getData(), column.getWidth());
		}
	}

	public static final class ColumnClick implements Listener {
		
		private final Menu columnsMenu;
		private final StateMachine stateMachine;
		
		@Inject public ColumnClick(ColumnsMenu columnsMenu, StateMachine stateMachine) {
			this.columnsMenu = columnsMenu;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event e) {
			// modify menu text a bit
			TableColumn tableColumn = (TableColumn) e.widget;
			Fetcher fetcher = (Fetcher) tableColumn.getData();
			
			MenuItem sortMenuItem = columnsMenu.getItem(0);
			MenuItem preferencesMenuItem = columnsMenu.getItem(1);
			MenuItem aboutMenuItem = columnsMenu.getItem(2);

			if (tableColumn.getParent().getSortColumn() == tableColumn)
				sortMenuItem.setText(Labels.getLabel("menu.columns.sortDirection"));
			else
				sortMenuItem.setText(Labels.getLabel("menu.columns.sortBy") + " " + fetcher.getName());

			// disable these menu items if scanning
			sortMenuItem.setEnabled(stateMachine.inState(ScanningState.IDLE));

			preferencesMenuItem.setText(fetcher.getName() + " " + Labels.getLabel("menu.columns.preferences"));
			preferencesMenuItem.setEnabled(fetcher.getPreferencesClass() != null && stateMachine.inState(ScanningState.IDLE));

			aboutMenuItem.setText(Labels.getLabel("menu.columns.about") + " " + fetcher.getName());
			
			// focus the table to make Enter work after using the menu
			tableColumn.getParent().forceFocus();
			
			// remember the clicked column (see SortBy, FetcherPreferences, and AboutFetcher below)
			columnsMenu.setData(tableColumn);
			
			// show the menu
			columnsMenu.setLocation(e.display.getCursorLocation());
			columnsMenu.setVisible(true);
		}
	}

	public static final class SortBy implements Listener {
		
		private final ScanningResultList scanningResultList;

		@Inject
		public SortBy(ScanningResultList scanningResultList) {
			this.scanningResultList = scanningResultList;
		}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			TableColumn tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();
			
			Table table = tableColumn.getParent();
			
			if (table.getSortColumn() != tableColumn) {
				table.setSortColumn(tableColumn);
				table.setSortDirection(SWT.UP);
			} 
			else {
				table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
			}

			scanningResultList.sort(table.indexOf(tableColumn), table.getSortDirection() == SWT.UP);
			((ResultTable)table).updateResults();
		}
	}
	
	public static final class FetcherPreferences implements Listener {
		
		private final FetcherRegistry fetcherRegistry;

		@Inject public FetcherPreferences(FetcherRegistry fetcherRegistry) {
			this.fetcherRegistry = fetcherRegistry;
		}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			TableColumn tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();
			
			Fetcher fetcher = (Fetcher) tableColumn.getData();
			
			fetcherRegistry.openPreferencesEditor(fetcher);
			
			// update name if preferences changed
			tableColumn.setText(fetcher.getFullName());
		}
	}
	
	public static final class AboutFetcher implements Listener {
		@Inject public AboutFetcher() {}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			TableColumn tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();
			
			Fetcher fetcher = (Fetcher) tableColumn.getData();

			MessageBox messageBox = new MessageBox(tableColumn.getParent().getShell(), SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setText(Labels.getLabel("text.fetchers.info") + fetcher.getName());
			String info = fetcher.getInfo();
			if (info == null) {
				info = Labels.getLabel("text.fetchers.info.notAvailable");
			}
			messageBox.setMessage(info);
			messageBox.open();
		}
	}

}
