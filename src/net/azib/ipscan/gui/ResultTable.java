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
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.fetchers.CommentFetcher;
import net.azib.ipscan.fetchers.MACFetcher;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.OpenerColumnFetcher;
import net.azib.ipscan.fetchers.OpenerLaunchFetcher;
import net.azib.ipscan.gui.actions.OpenerLauncher;
import net.azib.ipscan.gui.menu.ColumnVisibilityMenu;
import net.azib.ipscan.fetchers.FetcherRegistryUpdateListener;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import net.azib.ipscan.gui.actions.ToolsActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
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
	private StateMachine stateMachine;

	private Image[] listImages = new Image[ResultType.values().length];

	private Listener columnClickListener;

	private Listener columnResizeListener;

	private Text inlineCommentEditor;

	/** custom header hover tooltip (width-capped, wrapping) */
	private Shell tooltipShell;
	private Text tooltipLabel;

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
		this.stateMachine = stateMachine;

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

		// right-click on the column header shows the column visibility (toggle) menu,
		// otherwise the row context menu is used (it is set via setMenu by MainWindow)
		var columnVisibilityMenu = new ColumnVisibilityMenu(getShell(), fetcherRegistry, this);
		addListener(SWT.MenuDetect, new HeaderRightClickHandler(columnVisibilityMenu));

		// header hover tooltip (width-capped, wrapping)
		// header hover tooltip: the table header is a separate native control, so MouseHover
		// does not fire over it; we detect the header region via MouseMove instead
		addListener(SWT.MouseMove, new HeaderTooltipHandler());
		addListener(SWT.MouseExit, e -> hideTooltip());
		addListener(SWT.MouseDown, e -> hideTooltip());

		// listen to state machine events
		stateMachine.addTransitionListener(this);
	}

	/**
	 * Rebuild column list according to selected fetchers
	 */
	public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
		// detect a newly added fetcher (compared to what we already have results for)
		var newSelected = new ArrayList<Fetcher>(fetcherRegistry.getSelectedFetchers());
		var previouslyKnown = scanningResults.getFetchers();
		Fetcher addedFetcher = null;
		for (var f : newSelected) {
			if (!previouslyKnown.contains(f)) {
				addedFetcher = f;
				break;
			}
		}

		// re-align stored results to the new fetcher order (keeps old values, null for new slot)
		scanningResults.syncFetchers(newSelected);

		// remove all columns (the rows/results are kept so the scanned list stays visible)
		for (var column : getColumns()) {
			column.dispose();
		}

		// determine the visual order: use the saved order if it covers the current selection
		var savedOrder = guiConfig.getColumnOrder();
		var ordered = new ArrayList<Fetcher>(newSelected);
		if (savedOrder != null) {
			ordered.clear();
			for (var id : savedOrder) {
				for (var f : newSelected) {
					if (f.getId().equals(id)) {
						ordered.add(f);
						break;
					}
				}
			}
			// append any newly added fetchers not present in the saved order
			for (var f : newSelected) {
				if (!ordered.contains(f)) ordered.add(f);
			}
		}

		// the IP column must always be the very first column and must not be movable,
		// so its status icon (the blue/green lamp) stays fixed at the first position
		var ipFetcher = newSelected.stream()
				.filter(f -> IPFetcher.ID.equals(f.getId()))
				.findFirst().orElse(null);
		if (ipFetcher != null) {
			ordered.remove(ipFetcher);
			ordered.add(0, ipFetcher);
		}

		// add the new selected columns back, in the (possibly saved) order
		var idle = stateMachine.inState(ScanningState.IDLE);
		for (var fetcher : ordered) {
			// the Opener Launch column centers its triangle icon
			var style = fetcher.getId().equals(OpenerLaunchFetcher.ID) ? SWT.CENTER : SWT.NONE;
			var tableColumn = new TableColumn(this, style);
			tableColumn.setWidth(guiConfig.getColumnWidth(fetcher));
			tableColumn.setData(fetcher);	// this is used in some listeners in ColumnsActions
			// IP column is never movable; others only when not scanning
			tableColumn.setMoveable(idle && !IPFetcher.ID.equals(fetcher.getId()));
			tableColumn.addListener(SWT.Selection, columnClickListener);
			tableColumn.addListener(SWT.Resize, columnResizeListener);
			tableColumn.addListener(SWT.Move, e -> saveColumnOrder());
		}
		updateColumnNames();

		// force the (still present) rows to be re-rendered with the new column set
		clearAll();

		// persist the resulting column order (including any newly added column)
		saveColumnOrder();

		// if a new fetcher was added and we are not scanning, populate its column for the already scanned IPs
		if (addedFetcher != null && idle) {
			populateNewFetcher(addedFetcher);
		}
	}

	/**
	 * Runs the given fetcher for all already-scanned results so its newly added column
	 * gets filled with real data. Runs off the UI thread to avoid freezing the table.
	 */
	private void populateNewFetcher(Fetcher fetcher) {
		var results = scanningResults;
		var fetcherId = fetcher.getId();
		new Thread(() -> {
			try {
				// best-effort initialization (some fetchers read config in init)
				try { fetcher.init(null); } catch (Exception ignored) {}
				for (var i = 0; i < results.getItemCount(); i++) {
					var result = results.getResult(i);
					if (result == null || !result.isReady()) continue;
					try {
						var subject = new ScanningSubject(result.getAddress());
						// some fetchers (e.g. Comment) need the MAC, which is known after scanning
						if (result.getMac() != null)
							subject.setParameter(MACFetcher.ID, result.getMac());
						var value = fetcher.scan(subject);
						var index = results.getFetcherIndex(fetcherId);
						if (index >= 0 && index < result.getValues().size())
							result.setValue(index, value);
						final var row = i;
						final var finalValue = value;
						getDisplay().asyncExec(() -> {
							try { updateResult(row, fetcherId, finalValue); }
							catch (Exception ignored) {}
						});
					}
					catch (Exception e) {
						// skip this IP if the fetcher fails, continue with the rest
					}
				}
			}
			catch (Exception e) {
				// never let an exception in the background thread break anything
			}
		}, "FetcherPopulator-" + fetcherId).start();
	}

	/**
	 * Persist the current visual (drag-reordered) column order.
	 */
	private void saveColumnOrder() {
		try {
			var order = getColumnOrder();
			var saved = new String[getColumnCount()];
			for (var c = 0; c < saved.length; c++) {
				var modelCol = (order != null && c < order.length) ? order[c] : c;
				saved[c] = ((Fetcher) getColumn(modelCol).getData()).getId();
			}
			guiConfig.setColumnOrder(saved);
		}
		catch (Exception e) {
			// never let an exception break the SWT event loop
		}
	}

	/**
	 * Maps a visual column position to the underlying model (creation-order) column index.
	 * Table.getColumn(int) returns model order, but visual positions follow getColumnOrder().
	 */
	private int modelColumnIndex(int visualCol) {
		var order = getColumnOrder();
		if (order != null && visualCol >= 0 && visualCol < order.length)
			return order[visualCol];
		return visualCol;
	}
	
	public void updateColumnNames() {
		// set each column's header from the fetcher actually bound to that column,
		// so it stays correct regardless of drag-reordering or a differing registry order
		for (var c = 0; c < getColumnCount(); c++) {
			var fetcher = (Fetcher) getColumn(c).getData();
			getColumn(c).setText(fetcher.getFullName());
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
			try {
			var item = (TableItem)event.item;
			var tableIndex = indexOf(item);
			if (tableIndex < 0) return;

			var scanningResult = scanningResults.getResult(tableIndex);
			List<?> values = scanningResult.getValues();

			// set each visual column's text based on the fetcher bound to that column,
			// so drag-reordering and newly added (still empty) columns work correctly.
			// NOTE: Table.getColumn(i) returns the column in MODEL (creation) order, while the
			// visual position is given by getColumnOrder(); we must map them accordingly.
			var order = getColumnOrder();
			var columnCount = getColumnCount();
			for (var c = 0; c < columnCount; c++) {
				var modelCol = (order != null && c < order.length) ? order[c] : c;
				var fetcher = (Fetcher) getColumn(modelCol).getData();
				var fetcherIndex = scanningResults.getFetcherIndex(fetcher.getId());
				String text = "";
				if (fetcherIndex >= 0 && fetcherIndex < values.size()) {
					var value = values.get(fetcherIndex);
					if (value != null)
						text = value.toString();
				}
				item.setText(c, text);
			}
			item.setImage(0, listImages[scanningResult.getType().ordinal()]);
			}
			catch (Exception e) {
				// never let an exception in rendering one row break the whole virtual table
			}
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
				if (col < 0) return;

				// identify the clicked column by its bound fetcher (robust to drag-reordering).
				// col is a VISUAL position, so map it to the model column via getColumnOrder().
				var modelCol = modelColumnIndex(col);
				var clickedFetcher = (Fetcher) getColumn(modelCol).getData();
				var clickedId = clickedFetcher.getId();

				if (CommentFetcher.ID.equals(clickedId)) {
					openEditor(row, modelCol, item);
					return;
				}

				if (OpenerColumnFetcher.ID.equals(clickedId)) {
					showOpenerMenu(row, modelCol, item);
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
				var x = event.x;
				var col = -1;
				for (var c = 0; c < getColumnCount(); c++) {
					x -= getColumn(c).getWidth();
					if (x <= 0) {
						col = c;
						break;
					}
				}
				if (col < 0) return;

				// identify the clicked column by its bound fetcher (robust to drag-reordering).
				// col is a VISUAL position, so map it to the model column via getColumnOrder().
				var modelCol = modelColumnIndex(col);
				var clickedFetcher = (Fetcher) getColumn(modelCol).getData();
				if (!OpenerLaunchFetcher.ID.equals(clickedFetcher.getId())) return;

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
		if (openerName == null && openersConfig.iterator().hasNext()) {
			// no per-IP default configured: fall back to the first available Opener
			openerName = openersConfig.iterator().next();
		}
		if (openerName == null) return;

		var opener = openersConfig.getOpener(openerName);
		if (opener == null) return;

		try {
			openerLauncher.launch(opener, row);
		}
		catch (UserErrorException e) {
			// surface a meaningful error instead of failing silently
			var mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
			mb.setText(Labels.getLabel("title.error"));
			mb.setMessage(e.getMessage());
			mb.open();
		}
	}

	/**
	 * Right-click on the column header shows the column visibility (toggle) menu,
	 * while a right-click on a row shows the normal row context menu.
	 */
	final class HeaderRightClickHandler implements Listener {
		private final Menu columnVisibilityMenu;

		HeaderRightClickHandler(Menu columnVisibilityMenu) {
			this.columnVisibilityMenu = columnVisibilityMenu;
		}

		public void handleEvent(Event event) {
			try {
				// event.x/event.y are display-relative; convert to table-relative coordinates
				var tableLoc = toControl(event.x, event.y);
				// if the right-click is on the header, use the column visibility menu; otherwise keep the row menu
				if (tableLoc.y <= getHeaderHeight()) {
					setMenu(columnVisibilityMenu);
				}
				// else: leave whatever row context menu MainWindow assigned via setMenu()
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop
			}
		}
	}

	/**
	 * Shows a width-capped, wrapping tooltip with the hovered column's "about" text.
	 */
	final class HeaderTooltipHandler implements Listener {
		int lastCol = -1;

		public void handleEvent(Event event) {
			try {
				// SWT mouse-event coordinates are already control-relative (no toControl needed)
				var cx = event.x;
				var cy = event.y;

				// detect the header area: everything above the first row's top (or the header height)
				// is the header; never show over data rows
				var firstItem = getItem(0);
				var headerBottom = (firstItem != null) ? firstItem.getBounds().y : getHeaderHeight();
				if (headerBottom <= 0) headerBottom = getHeaderHeight();
				if (cy >= headerBottom) {
					hideTooltip();
					lastCol = -1;
					return;
				}

				// determine which visual column is under the cursor
				var x = cx;
				var col = -1;
				for (var c = 0; c < getColumnCount(); c++) {
					x -= getColumn(c).getWidth();
					if (x <= 0) {
						col = c;
						break;
					}
				}
				if (col < 0) {
					hideTooltip();
					lastCol = -1;
					return;
				}

				var modelCol = modelColumnIndex(col);
				var fetcher = (Fetcher) getColumn(modelCol).getData();
				var info = fetcher.getInfo();
				if (info == null || info.isEmpty()) {
					hideTooltip();
					lastCol = -1;
					return;
				}

				// position the tooltip under the hovered column header, at the cursor's x
				var loc = toDisplay(cx, headerBottom);
				if (col != lastCol) {
					showTooltip(info, loc.x, loc.y);
					lastCol = col;
				}
				else {
					moveTooltip(loc.x, loc.y);
				}
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop
			}
		}
	}

	/** maximum width of the header hover tooltip, in pixels */
	private static final int TOOLTIP_MAX_WIDTH = 360;

	private void showTooltip(String text, int displayX, int displayY) {
		if (tooltipShell == null || tooltipShell.isDisposed()) {
			tooltipShell = new Shell(getShell(), SWT.ON_TOP | SWT.TOOL | SWT.NO_FOCUS);
			var layout = new GridLayout(1, false);
			layout.marginWidth = 4;
			layout.marginHeight = 3;
			tooltipShell.setLayout(layout);

			tooltipLabel = new Text(tooltipShell, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
			tooltipLabel.setEditable(false);
			tooltipLabel.setBackground(getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			tooltipLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			tooltipLabel.setLayoutData(new GridData(TOOLTIP_MAX_WIDTH, SWT.DEFAULT));
		}

		tooltipLabel.setText(text);
		tooltipShell.pack();

		// place under the hovered column header, at the cursor's x
		tooltipShell.setLocation(displayX, displayY);
		tooltipShell.setVisible(true);
	}

	private void moveTooltip(int displayX, int displayY) {
		if (tooltipShell != null && !tooltipShell.isDisposed() && tooltipShell.isVisible()) {
			tooltipShell.setLocation(displayX, displayY);
		}
	}

	private void hideTooltip() {
		if (tooltipShell != null && !tooltipShell.isDisposed()) {
			tooltipShell.setVisible(false);
		}
	}

	public void transitionTo(ScanningState state, Transition transition) {
		// change cursor while scanning
		setCursor(getDisplay().getSystemCursor(state == ScanningState.IDLE ? SWT.CURSOR_ARROW : SWT.CURSOR_APPSTARTING));

		// disable column drag-reordering while scanning (it can misalign the virtual table mid-repaint),
		// re-enable it when idle
		setColumnsMoveable(state == ScanningState.IDLE);
	}

	/**
	 * Enables or disables drag-to-reorder of all columns.
	 */
	private void setColumnsMoveable(boolean moveable) {
		try {
			for (var c = 0; c < getColumnCount(); c++) {
				var col = (orderSafe(c));
				if (col != null) col.setMoveable(moveable);
			}
		}
		catch (Exception e) {
			// never let an exception break the SWT event loop
		}
	}

	private TableColumn orderSafe(int visualCol) {
		try {
			var order = getColumnOrder();
			var modelCol = (order != null && visualCol < order.length) ? order[visualCol] : visualCol;
			return getColumn(modelCol);
		}
		catch (Exception e) {
			return null;
		}
	}
}
