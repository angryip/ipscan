/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class ColumnsActions {
	
	public static final class ColumnResize implements Listener {
		private GUIConfig guiConfig;
		
		public ColumnResize(GUIConfig guiConfig) {
			this.guiConfig = guiConfig;
		}

		public void handleEvent(Event event) {
			var column = (TableColumn) event.widget;
			var table = column.getParent();
			// do not save the width of the last (visual) column on Linux, because in GTK 
			// it is stretched to the width of the whole table and therefore is incorrect
			var order = table.getColumnOrder();
			var lastVisualModelIndex = (order != null && order.length > 0) ? order[order.length - 1] : table.getColumnCount() - 1;
			if (Platform.LINUX && table.getColumn(lastVisualModelIndex) == column)
				return;

			// save column width
			guiConfig.setColumnWidth((Fetcher)column.getData(), column.getWidth());
		}
	}

	public static final class ColumnClick implements Listener {
		
		private final ScanningResultList scanningResultList;
		private final StateMachine stateMachine;
		
		public ColumnClick(ScanningResultList scanningResultList, StateMachine stateMachine) {
			this.scanningResultList = scanningResultList;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event e) {
			// a (left) click on a column header sorts by that column; clicking it again
			// reverses the sort direction. The column (right-click) menu is shown separately.
			// Selection events on a TableColumn carry button == 0, so we only skip right-clicks.
			if (e.button == 3) return;
			if (!stateMachine.inState(ScanningState.IDLE)) return;

			var tableColumn = (TableColumn) e.widget;
			var table = tableColumn.getParent();

			if (table.getSortColumn() != tableColumn) {
				table.setSortColumn(tableColumn);
				table.setSortDirection(SWT.UP);
			}
			else {
				table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
			}

			// sort by the data (fetcher) index of the clicked column, not its visual position,
			// so drag-reordering of columns does not break sorting
			var fetcher = (Fetcher) tableColumn.getData();
			var fetcherIndex = scanningResultList.getFetcherIndex(fetcher.getId());
			if (fetcherIndex < 0) return;

			scanningResultList.sort(fetcherIndex, table.getSortDirection() == SWT.UP);
			((ResultTable)table).updateResults();
		}
	}

	public static final class SortBy implements Listener {
		private final ScanningResultList scanningResultList;

		public SortBy(ScanningResultList scanningResultList) {
			this.scanningResultList = scanningResultList;
		}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			var tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();

			var table = tableColumn.getParent();
			
			if (table.getSortColumn() != tableColumn) {
				table.setSortColumn(tableColumn);
				table.setSortDirection(SWT.UP);
			} 
			else {
				table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
			}

			// sort by the data (fetcher) index of the clicked column, not its visual position,
			// so drag-reordering of columns does not break sorting
			var fetcher = (Fetcher) tableColumn.getData();
			var fetcherIndex = scanningResultList.getFetcherIndex(fetcher.getId());
			if (fetcherIndex < 0) return;

			scanningResultList.sort(fetcherIndex, table.getSortDirection() == SWT.UP);
			((ResultTable)table).updateResults();
		}
	}
	
	public static final class FetcherPreferences implements Listener {
		private final FetcherRegistry fetcherRegistry;

		public FetcherPreferences(FetcherRegistry fetcherRegistry) {
			this.fetcherRegistry = fetcherRegistry;
		}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			var tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();

			var fetcher = (Fetcher) tableColumn.getData();
			
			fetcherRegistry.openPreferencesEditor(fetcher);
			
			// update name if preferences changed
			tableColumn.setText(fetcher.getFullName());
		}
	}
	
	public static final class AboutFetcher implements Listener {
		public AboutFetcher() {}

		public void handleEvent(Event event) {
			// retrieve the clicked column (see ColumnClick above)
			var tableColumn = (TableColumn) ((MenuItem)event.widget).getParent().getData();

			var fetcher = (Fetcher) tableColumn.getData();

			var messageBox = new MessageBox(tableColumn.getParent().getShell(), SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setText(Labels.getLabel("text.fetchers.info") + fetcher.getName());
			var info = fetcher.getInfo();
			if (info == null) {
				info = Labels.getLabel("text.fetchers.info.notAvailable");
			}
			messageBox.setMessage(info);
			messageBox.open();
		}
	}

}
