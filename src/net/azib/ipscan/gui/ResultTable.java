/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.config.DefaultOpenerConfig;
import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.fetchers.CommentFetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.OpenerColumnFetcher;
import net.azib.ipscan.fetchers.OpenerLaunchFetcher;
import net.azib.ipscan.gui.actions.OpenerLauncher;
import net.azib.ipscan.fetchers.FetcherRegistryUpdateListener;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import net.azib.ipscan.gui.actions.ToolsActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import java.util.List;

import static net.azib.ipscan.core.ScanningResult.ResultType.*;
import static net.azib.ipscan.gui.util.LayoutHelper.icon;

/**
 * Table of scanning results.
 * 
 * @author Anton Keks
 */
	public class ResultTable extends Table implements FetcherRegistryUpdateListener, StateTransitionListener {
	private ScanningResultList scanningResults;
	private GUIConfig guiConfig;
	private FetcherRegistry fetcherRegistry;
	private CommentsConfig commentsConfig;
	private OpenersConfig openersConfig;
	private DefaultOpenerConfig defaultOpenerConfig;
	private OpenerLauncher openerLauncher;

	private Image[] listImages = new Image[ResultType.values().length];

	private Listener columnClickListener;

	private Listener columnResizeListener;

	private Text inlineCommentEditor;

	public ResultTable(Shell parent, GUIConfig guiConfig, FetcherRegistry fetcherRegistry, CommentsConfig commentsConfig,
							   ScanningResultList scanningResultList, StateMachine stateMachine,
							   ColumnsActions.ColumnClick columnClickListener, ColumnsActions.ColumnResize columnResizeListener,
							   OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig, OpenerLauncher openerLauncher) {
		super(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.guiConfig = guiConfig;
		this.scanningResults = scanningResultList;
		this.fetcherRegistry = fetcherRegistry;
		this.commentsConfig = commentsConfig;
		this.openersConfig = openersConfig;
		this.defaultOpenerConfig = defaultOpenerConfig;
		this.openerLauncher = openerLauncher;

		setHeaderVisible(true);
		setLinesVisible(true);

		this.columnClickListener = columnClickListener;
		this.columnResizeListener = columnResizeListener;
		fetcherRegistry.addListener(this);
		// add columns according to fetchers
		handleUpdateOfSelectedFetchers(fetcherRegistry);

		// load button images
		listImages[UNKNOWN.ordinal()] = icon("list/unknown");
		listImages[DEAD.ordinal()] = icon("list/dead");
		listImages[ALIVE.ordinal()] = icon("list/alive");
		listImages[WITH_PORTS.ordinal()] = icon("list/ports");

		addListener(SWT.KeyDown, new CommandsMenuActions.Delete(this, stateMachine));
		addListener(SWT.KeyDown, new CommandsMenuActions.CopyIP(this));
		addListener(SWT.KeyDown, new ToolsActions.SelectAll(this));

		// this one populates table dynamically, taking data from ScanningResultList
		addListener(SWT.SetData, new SetDataListener());

		// double-click on the comment cell opens an inline editor, on the opener cell a dropdown to pick an Opener
		addListener(SWT.MouseDoubleClick, new DoubleClickColumnHandler());

		// single-click on the Opener column's triangle icon launches the default Opener for that IP
		addListener(SWT.MouseDown, new OpenerIconClickHandler());

		// listen to state machine events
		stateMachine.addTransitionListener(this);
	}

	/**
	 * Rebuild column list according to selected fetchers
	 */
	public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
		// remove all items (otherwise they will be shown incorrectly)
		removeAll();
		
		// remove all columns
		for (var column : getColumns()) {
			column.dispose();
		}
		
		// add the new selected columns back
		for (var fetcher : fetcherRegistry.getSelectedFetchers()) {
			// the Opener Launch column centers its triangle icon
			var style = fetcher.getId().equals(OpenerLaunchFetcher.ID) ? SWT.CENTER : SWT.NONE;
			var tableColumn = new TableColumn(this, style);
			tableColumn.setWidth(guiConfig.getColumnWidth(fetcher));
			tableColumn.setData(fetcher);	// this is used in some listeners in ColumnsActions
			tableColumn.addListener(SWT.Selection, columnClickListener);
			tableColumn.addListener(SWT.Resize, columnResizeListener);
		}
		updateColumnNames();
	}
	
	public void updateColumnNames() {
		var i = 0;
		for (var fetcher : fetcherRegistry.getSelectedFetchers()) {
			getColumn(i++).setText(fetcher.getFullName());
		}
	}

	protected void checkSubclass() {
		// This method is overridden and does nothing in order to
		// be able to subclass the Table. We are not going to 
		// override anything important, so this should be safe (tm)
	}

	/**
	 * Adds the specified results holder to the table and registers it
	 * in the ScanningResultList instance or just redraws the corresponding row
	 * if the result is already present.
	 * <p/>
	 * Note: this method may be called from any thread.
	 * 
	 * @param result
	 */
	public void addOrUpdateResultRow(final ScanningResult result) {
		if (isDisposed())
			return;
		getDisplay().asyncExec(() -> {
			if (isDisposed())
				return;

			if (scanningResults.isRegistered(result)) {
				// just redraw the item
				var index = scanningResults.update(result);
				clear(index);
			}
			else {
				// first register, then add - otherwise first redraw may fail (the table is virtual)
				var index = getItemCount();
				scanningResults.registerAtIndex(index, result);
				// setItemCount(index+1) - this seems to rebuild TableItems inside, so is slower
				new TableItem(ResultTable.this, SWT.NONE);
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
	 * Changes the specified value
	 * @param fetcherId
	 * @param newValue
	 */
	public void updateResult(int index, String fetcherId, Object newValue) {
		var fetcherIndex = scanningResults.getFetcherIndex(fetcherId);
		if (fetcherIndex >= 0) {
			// update the value in the results
			scanningResults.getResult(index).setValue(fetcherIndex, newValue);
			// update visual representation
			clear(index);
		}
	}

	/**
	 * Returns the currently seelcted resusult
	 * @return
	 */
	public ScanningResult getSelectedResult() {
		var selectedIndex = getSelectionIndex();
		return selectedIndex >= 0 ? scanningResults.getResult(selectedIndex) : null;
	}
	
	public void remove(int[] indices) {
		// we need to remove the elements from our real storage as well
		scanningResults.remove(indices);
		super.remove(indices);
		// TODO: this is VERY slow if there are a lot of items (eg 300k), due to the Control.sort() that is called inside to sort the indices
	}
	
	public void removeAll() {
		// remove all items from the real storage first
		scanningResults.clear();
		super.removeAll();
		setSortColumn(null);
	}
	
	/**
	 * Resets selected items as if they were just added to the table.
	 * This is used for removing of any scanned data for rescanning of items.
	 */
	public void resetSelection() {
		var selectionIndices = getSelectionIndices();
		// clear scanning results
		for (var itemNum : selectionIndices) {
			scanningResults.getResult(itemNum).reset();
		}
		// redraw items in the table
		clear(selectionIndices);
	}

	/**
	 * @return the internal ScanningResultList instance, containing the results.
	 */
	public ScanningResultList getScanningResults() {
		return scanningResults;
	}

	/**
	 * This listener is used for displaying the real results in the table, on demand.
	 */
	final class SetDataListener implements Listener {

		public void handleEvent(Event event) {
			var item = (TableItem)event.item;
			var tableIndex = indexOf(item);
			if (tableIndex < 0) return;

			var scanningResult = scanningResults.getResult(tableIndex);
			List<?> values = scanningResult.getValues();
			var resultStrings = new String[values.size()];
			for (var i = 0; i < values.size(); i++) {
				var value = values.get(i);
				if (value != null)
					resultStrings[i] = value.toString();
			}			 
			item.setText(resultStrings);
			item.setImage(0, listImages[scanningResult.getType().ordinal()]);
		}
	}

	/**
	 * Handles double-clicks on the result table:
	 * - comment cell: opens an inline text editor (saved on focus loss or Enter)
	 * - opener cell: shows a dropdown menu of all configured Openers to pick the default one
	 */
	final class DoubleClickColumnHandler implements Listener {
		public void handleEvent(Event event) {
			try {
				if (event.button != 1) return;

				var item = getItem(new Point(event.x, event.y));
				if (item == null) return;

				var row = indexOf(item);
				if (row < 0) return;

				// determine which column was double-clicked based on x offset
				var x = event.x;
				var col = -1;
				for (var c = 0; c < getColumnCount(); c++) {
					x -= getColumn(c).getWidth();
					if (x <= 0) {
						col = c;
						break;
					}
				}

				var commentColumn = scanningResults.getFetcherIndex(CommentFetcher.ID);
				if (commentColumn >= 0 && col == commentColumn) {
					openEditor(row, commentColumn, item);
					return;
				}

				var openerColumn = scanningResults.getFetcherIndex(OpenerColumnFetcher.ID);
				if (openerColumn >= 0 && col == openerColumn) {
					showOpenerMenu(row, openerColumn, item);
					return;
				}
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop (which would also break the context menu)
			}
		}

		private void openEditor(int row, int column, TableItem item) {
			if (row < 0 || row >= getItemCount()) return;

			if (inlineCommentEditor != null && !inlineCommentEditor.isDisposed())
				inlineCommentEditor.dispose();

			var result = scanningResults.getResult(row);
			if (result == null) return;
			var current = commentsConfig.getComment(result);

			var rect = item.getTextBounds(column);
			if (rect == null) return;

			inlineCommentEditor = new Text(ResultTable.this, SWT.BORDER | SWT.SINGLE);
			inlineCommentEditor.setText(current != null ? current : "");
			inlineCommentEditor.setBounds(rect);
			inlineCommentEditor.selectAll();
			inlineCommentEditor.setFocus();

			var commit = new Runnable() {
				public void run() {
					if (inlineCommentEditor == null || inlineCommentEditor.isDisposed()) return;
					var newComment = inlineCommentEditor.getText();
					inlineCommentEditor.dispose();
					inlineCommentEditor = null;
					commentsConfig.setComment(result, newComment);
					updateResult(row, CommentFetcher.ID, newComment);
				}
			};

			inlineCommentEditor.addListener(SWT.FocusOut, e -> commit.run());
			inlineCommentEditor.addListener(SWT.Traverse, e -> {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					commit.run();
					e.doit = false;
				}
				else if (e.detail == SWT.TRAVERSE_ESCAPE) {
					if (inlineCommentEditor != null && !inlineCommentEditor.isDisposed()) {
						inlineCommentEditor.dispose();
						inlineCommentEditor = null;
					}
					e.doit = false;
				}
			});
		}

		private void showOpenerMenu(int row, int column, TableItem item) {
			if (row < 0 || row >= getItemCount()) return;
			var result = scanningResults.getResult(row);
			if (result == null) return;

			var rect = item.getTextBounds(column);
			if (rect == null) return;

			var menu = new Menu(ResultTable.this.getShell(), SWT.POP_UP);
			var current = defaultOpenerConfig.get(result.getAddress().getHostAddress());

			for (var name : openersConfig) {
				var menuItem = new MenuItem(menu, SWT.RADIO);
				menuItem.setText(name);
				menuItem.setSelection(name.equals(current));
				menuItem.addListener(SWT.Selection, e -> {
					for (var i : getSelectionIndices()) {
						var ip = scanningResults.getResult(i).getAddress().getHostAddress();
						defaultOpenerConfig.set(ip, name);
						updateResult(i, OpenerColumnFetcher.ID, name);
					}
				});
			}

			// a "None" entry to clear the default Opener
			var none = new MenuItem(menu, SWT.RADIO);
			none.setText(Labels.getLabel("menu.commands.setOpener.none"));
			none.setSelection(current == null);
			none.addListener(SWT.Selection, e -> {
				for (var i : getSelectionIndices()) {
					var ip = scanningResults.getResult(i).getAddress().getHostAddress();
					defaultOpenerConfig.set(ip, null);
					updateResult(i, OpenerColumnFetcher.ID, "—");
				}
			});

			var location = ResultTable.this.toDisplay(rect.x, rect.y + rect.height);
			menu.setLocation(location);
			menu.setVisible(true);
		}
	}

	/**
	 * Single-click on the triangle icon in the Opener column launches the IP
	 * using its configured default Opener.
	 */
	final class OpenerIconClickHandler implements Listener {
		public void handleEvent(Event event) {
			try {
				if (event.button != 1) return;

				var item = getItem(new Point(event.x, event.y));
				if (item == null) return;

				var row = indexOf(item);
				if (row < 0 || row >= getItemCount()) return;

				// the whole Opener Launch column is clickable
				var launchCol = scanningResults.getFetcherIndex(OpenerLaunchFetcher.ID);
				if (launchCol < 0) return;
				var x = event.x;
				var col = -1;
				for (var c = 0; c < getColumnCount(); c++) {
					x -= getColumn(c).getWidth();
					if (x <= 0) {
						col = c;
						break;
					}
				}
				if (col != launchCol) return;

				openWithDefaultOpener(row);
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop (which would also break the context menu)
			}
		}
	}

	private void openWithDefaultOpener(int row) {
		var result = scanningResults.getResult(row);
		if (result == null) return;

		var ip = result.getAddress().getHostAddress();
		var openerName = defaultOpenerConfig.get(ip);
		if (openerName == null) return;

		var opener = openersConfig.getOpener(openerName);
		if (opener == null) return;

		openerLauncher.launch(opener, row);
	}

	public void transitionTo(ScanningState state, Transition transition) {
		// change cursor while scanning
		setCursor(getDisplay().getSystemCursor(state == ScanningState.IDLE ? SWT.CURSOR_ARROW : SWT.CURSOR_APPSTARTING));
	}
}
