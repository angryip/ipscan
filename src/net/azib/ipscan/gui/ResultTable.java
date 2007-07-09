/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import java.net.InetAddress;
import java.util.List;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.FetcherRegistryUpdateListener;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsActions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Table of scanning results.
 * 
 * @author anton
 */
public class ResultTable extends Table implements FetcherRegistryUpdateListener {
		
	private ScanningResultList scanningResults;
	
	private Image[] listImages = new Image[4];

	private String feederInfo;

	private Listener columnClickListener;

	private Listener columnResizeListener;

	public ResultTable(Composite parent, FetcherRegistry fetcherRegistry, ScanningResultList scanningResultList, ColumnsActions.ColumnClick columnClickListener, ColumnsActions.ColumnResize columnResizeListener) {
		super(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.scanningResults = scanningResultList;
		
		setHeaderVisible(true);
		setLinesVisible(true);
		
		this.columnClickListener = columnClickListener;
		this.columnResizeListener = columnResizeListener;
		fetcherRegistry.addListener(this);
		handleUpdateOfSelectedFetchers(fetcherRegistry);
		
		// pre-load button images
		listImages[ScanningSubject.RESULT_TYPE_UNKNOWN] = new Image(null, Labels.getInstance().getImageAsStream("list.unknown.img"));
		listImages[ScanningSubject.RESULT_TYPE_DEAD] = new Image(null, Labels.getInstance().getImageAsStream("list.dead.img"));
		listImages[ScanningSubject.RESULT_TYPE_ALIVE] = new Image(null, Labels.getInstance().getImageAsStream("list.alive.img"));
		listImages[ScanningSubject.RESULT_TYPE_ADDITIONAL_INFO] = new Image(null, Labels.getInstance().getImageAsStream("list.addinfo.img"));
		
		Listener detailsListener = new Listener() {
			CommandsActions.Details detailsListener = new CommandsActions.Details(ResultTable.this);
			public void handleEvent(Event e) {
				if (e.type == SWT.MouseDoubleClick || e.detail == SWT.TRAVERSE_RETURN) {
					detailsListener.handleEvent(e);
					e.doit = false;
				}
			}
		};
		addListener(SWT.Traverse, detailsListener);
		addListener(SWT.MouseDoubleClick, detailsListener);
		addListener(SWT.KeyDown, new CommandsActions.Delete(this));
		
		addListener(SWT.SetData, new SetDataListener());
	}

	/**
	 * Rebuild column list according to selected fetchers
	 */
	public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
		// remove all items (otherwise they will be shown incorrectly)
		removeAll();
		
		// remove all columns
		TableColumn[] columns = getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].dispose();
		}
		
		// add the new selected columns back
		for (Fetcher fetcher : fetcherRegistry.getSelectedFetchers()) {
			TableColumn tableColumn = new TableColumn(this, SWT.NONE);
			String fetcherName = Labels.getLabel(fetcher.getLabel());
			tableColumn.setWidth(Config.getDimensionsConfig().getColumnWidth(fetcherName));
			tableColumn.setText(fetcherName);
			tableColumn.setData(fetcher);	// this is used in some listeners in ColumnsActions
			tableColumn.addListener(SWT.Selection, columnClickListener);
			tableColumn.addListener(SWT.Resize, columnResizeListener);
		}
	}

	protected void checkSubclass() {
		// This method is overriden and does nothing in order to
		// be able to subclass the Table. We are not going to 
		// override anything important, so this should be safe (tm)
	}

	public int addResultsRow(final InetAddress address) {
		if (isDisposed())
			return 0;
		final int index = scanningResults.add(address);
		getDisplay().syncExec(new Runnable() {
			public void run() {
				if (index >= getItemCount()) 
					setItemCount(index+1);
			}
		});
		return index;
	}

	/**
	 * Forces the specified element to be redrawn.
	 * This method can be called from any thread.
	 * @param index
	 */
	public void updateResults(final int index) {
		if (isDisposed())
			return;
		getDisplay().syncExec(new Runnable() {
			public void run() {
				// redraw the item
				ResultTable.this.clear(index);
			}
		});
	}
	
	/**
	 * Forces all elements to be redrawn
	 */
	public void updateResults() {
		clearAll();
	}

	/**
	 * Returns the details about the currently selected IP address
	 * @return
	 */
	public String getIPDetails() {
		int selectedIndex = getSelectionIndex();
		return scanningResults.getResultsAsString(selectedIndex);
	}
	
	public void remove(int[] indices) {
		// we need to remove the elements from our real storage as well
		scanningResults.remove(indices);
		super.remove(indices);
	}
	
	public void removeAll() {
		// remove all items from the real storage first
		scanningResults.clear();
		super.removeAll();
	}
	
	public void resetSelection() {
		int columnCount = getColumnCount();
		for (TableItem item : getSelection()) {
			for (int i = 1; i < columnCount; i++) {
				item.setText(i, "");
			}
			item.setImage(this.listImages[ScanningSubject.RESULT_TYPE_UNKNOWN]);
		}
	}

	/**
	 * Initializes a new scan.
	 * (clears all elments, etc)
	 * @param newFeederInfo feeder info of the new feeder/settings
	 */
	public void initNewScan(String newFeederInfo) {
		// initialize new feeder info
		this.feederInfo = newFeederInfo; 
		// remove all items from the table
		removeAll();
	}
		
	/**
	 * @return the internal ScanningResultList instance, containing the results.
	 */
	public ScanningResultList getScanningResults() {
		return scanningResults;
	}

	/**
	 * @return the feeder info, which used in this scan
	 */
	public String getFeederInfo() {
		return feederInfo;
	}

	/**
	 * This listener is used for displaying the real results in the table, on demand.
	 */
	class SetDataListener implements Listener {

		public void handleEvent(Event event) {
			TableItem item = (TableItem)event.item;
			int tableIndex = indexOf(item);
			
			ScanningResult scanningResult = scanningResults.getResult(tableIndex);
			List<Object> values = scanningResult.getValues();
			String[] resultStrings = new String[values.size()];
			for (int i = 0; i < values.size(); i++) {				
				Object value = values.get(i);
				if (value != null)
					resultStrings[i] = value.toString();
			}			 
			item.setText(resultStrings);
			item.setImage(0, listImages[scanningResult.getType()]);
		}
		
	}

}
