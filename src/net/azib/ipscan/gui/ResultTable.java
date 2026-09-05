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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

	/** guards against queued-up Opener launches when the triangle is clicked rapidly on several rows */
	private final AtomicBoolean launching = new AtomicBoolean(false);
	private static final int LAUNCH_COOLDOWN_MS = 1500;

	/** column (model) index -> fetcher index in ScanningResultList, precomputed to avoid per-cell lookups during virtual table rendering */
	private int[] columnFetcherIndexMap;

	/** custom header hover tooltip (width-capped, wrapping) */
	private Shell tooltipShell;
	private Text tooltipLabel;

	private Menu rowContextMenu;

	/**
	 * Sizes every fetcher column to fit its content (the leading autofit button
	 * column itself is skipped). Triggered by clicking the "<->" header button.
	 */
	private void autoFitAllColumns() {
		try {
			if (isDisposed() || !stateMachine.inState(ScanningState.IDLE)) return;
			for (var column : getColumns()) {
				if (isDisposed()) return;
				if (column.getData() instanceof Fetcher && column.getResizable())
					column.pack();
			}
		}
		catch (Exception e) {
			// never let an exception break the SWT event loop
		}
	}

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
		// On Win32 the table header is a separate native control, so Table-level mouse events
		// don't fire over it, and even Display-level SWT.MouseMove events do not reach SWT
		// for the native SysHeader32. Poll cursor position via timer instead (see PR #527).
		var tooltip = new HeaderTooltipPoller();
		addListener(SWT.Dispose, e -> tooltip.dispose());

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

		// fixed leading button column that auto-sizes all columns (mirrors the FX autofit button).
		// It carries no Fetcher (getData() == null), is never movable/resizable/sortable,
		// and its cells stay empty; every fetcher-bound logic must skip it via instanceof checks.
		var autofitColumn = new TableColumn(this, SWT.CENTER);
		autofitColumn.setResizable(false);
		autofitColumn.setMoveable(false);
		autofitColumn.setText("<->");
		autofitColumn.setData(null);
		autofitColumn.addListener(SWT.Selection, e -> autoFitAllColumns());
		// NOTE: the column is sized (pack()) only after clearAll() below — at this
		// point the table items may still hold stale text from the previous column
		// set, which would make pack() measure garbage and give a random width

		// add the new selected columns back, in the (possibly saved) order
		var idle = stateMachine.inState(ScanningState.IDLE);
		for (var fetcher : ordered) {
			// the Opener Launch column centers its triangle icon
			var style = fetcher.getId().equals(OpenerLaunchFetcher.ID) ? SWT.CENTER : SWT.NONE;
			var tableColumn = new TableColumn(this, style);
			tableColumn.setWidth(guiConfig.getColumnWidth(fetcher));
			tableColumn.setData(fetcher);	// this is used in some listeners in ColumnsActions
			tableColumn.setText(fetcher.getFullName());  // set the header name immediately while the column is freshly created
			// IP column is never movable; others only when not scanning
			tableColumn.setMoveable(idle && !IPFetcher.ID.equals(fetcher.getId()));
			tableColumn.addListener(SWT.Selection, columnClickListener);
			tableColumn.addListener(SWT.Resize, columnResizeListener);
			tableColumn.addListener(SWT.Move, e -> {
				// defer saving of the new visual order — SWT on Windows may not have
				// updated getColumnOrder() yet when this native event fires
				getDisplay().asyncExec(() -> {
					if (!isDisposed()) saveColumnOrder();
				});
			});
		}

		// precompute column-to-fetcher-index mapping for fast virtual-table rendering
		// (the leading autofit button column has no Fetcher and maps to -1)
		var colCount = getColumnCount();
		columnFetcherIndexMap = new int[colCount];
		for (var c = 0; c < colCount; c++) {
			var data = getColumn(c).getData();
			columnFetcherIndexMap[c] = data instanceof Fetcher fetcher ? scanningResults.getFetcherIndex(fetcher.getId()) : -1;
		}

		// force the (still present) rows to be re-rendered with the new column set
		clearAll();

		// now the items are cleared, so pack() sizes the autofit column exactly to
		// its "<->" header text (pack() accounts for the native header margins and
		// the current DPI scale) — keeping it a fixed small width on every rebuild
		autofitColumn.pack();

		// force a full repaint: after a column rebuild Win32 does not always
		// invalidate the table's client area (e.g. when a modal dialog that
		// covered the table has just closed), leaving the results blank until
		// the mouse moves over them
		try {
			redraw();
			update();
		}
		catch (Exception e) {
			// never let an exception break the SWT event loop
		}

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
		// batch UI updates: one asyncExec per 100 rows instead of one per row
		final var batchSize = 100;
		new Thread(() -> {
			try {
				// best-effort initialization (some fetchers read config in init)
				try { fetcher.init(null); } catch (Exception ignored) {}

				// refresh immediately, BEFORE any (potentially slow) network scans run:
				// the rebuilt column set (incl. the new header) and the still-empty new
				// column must appear right away, not only after the first batch finishes
				getDisplay().asyncExec(() -> {
					if (isDisposed()) return;
					try {
						setRedraw(false);
						clearAll();
					}
					catch (Exception ignored) {
					}
					finally {
						try { setRedraw(true); } catch (Exception ignored) {}
					}
					try {
						nudgeLastColumnForHeaderRepaint();
						update();
					}
					catch (Exception ignored) {}
				});

				var total = results.getItemCount();
				var position = results.getFetcherIndex(fetcherId);
				for (var batchStart = 0; batchStart < total; batchStart += batchSize) {
					var batchEnd = Math.min(batchStart + batchSize, total);
					for (var i = batchStart; i < batchEnd; i++) {
						var result = results.getResult(i);
						if (result == null || !result.isReady()) continue;
						try {
							var subject = new ScanningSubject(result.getAddress());
							// some fetchers (e.g. Comment) need the MAC, which is known after scanning
							if (result.getMac() != null)
								subject.setParameter(MACFetcher.ID, result.getMac());
							var value = fetcher.scan(subject);
							if (position >= 0 && position < result.getValues().size())
								result.setValue(position, value);
						}
						catch (Exception e) {
							// skip this IP if the fetcher fails, continue with the rest
						}
					}
					final var start = batchStart;
					final var end = batchEnd;
					getDisplay().asyncExec(() -> {
						if (isDisposed()) return;
						try {
							setRedraw(false);
							for (var r = start; r < end; r++)
								clear(r);
						}
						catch (Exception ignored) {
						}
						finally {
							try {
								setRedraw(true);
								update();
							}
							catch (Exception ignored) {}
						}
					});
				}
			}
			catch (Exception e) {
				// never let an exception in the background thread break anything
			}
		}, "FetcherPopulator-" + fetcherId).start();
	}

	/**
	 * Win32 quirk: the native header (SysHeader32) does not always invalidate
	 * when columns are recreated while the table has items. Nudging the last
	 * column's width by 1px and back forces the header to repaint immediately.
	 */
	private void nudgeLastColumnForHeaderRepaint() {
		var cols = getColumns();
		if (cols.length == 0) return;
		var last = cols[cols.length - 1];
		var width = last.getWidth();
		last.setWidth(width + 1);
		last.setWidth(width);
	}

	/**
	 * Persist the current visual (drag-reordered) column order.
	 */
	private void saveColumnOrder() {
		try {
			// persist only fetcher-bound columns in visual order; the leading
			// autofit button column is fixed and not part of the saved order
			var order = getColumnOrder();
			var ids = new ArrayList<String>();
			for (var c = 0; c < getColumnCount(); c++) {
				var modelCol = (order != null && c < order.length) ? order[c] : c;
				var data = getColumn(modelCol).getData();
				if (data instanceof Fetcher fetcher) ids.add(fetcher.getId());
			}
			guiConfig.setColumnOrder(ids.toArray(new String[0]));
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
		// after a drag-reorder on Windows, the native header control may map
		// setText() calls to wrong positions based on stale SWT-internal indices.
		// Rebuild the columns from scratch to guarantee correct header names.
		handleUpdateOfSelectedFetchers(fetcherRegistry);
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

	public void updateOpenerAssignment(int index, String openerName) {
		var colIndex = scanningResults.getFetcherIndex(OpenerColumnFetcher.ID);
		var launchIndex = scanningResults.getFetcherIndex(OpenerLaunchFetcher.ID);
		var result = scanningResults.getResult(index);
		if (result == null) return;
		if (colIndex >= 0) result.setValue(colIndex, openerName);
		if (launchIndex >= 0) result.setValue(launchIndex, openerName != null ? "▶" : "");
		if (colIndex >= 0 || launchIndex >= 0) clear(index);
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
				// the leading autofit button column carries no Fetcher: leave its cells empty
				if (!(getColumn(modelCol).getData() instanceof Fetcher fetcher)) {
					item.setText(modelCol, "");
					continue;
				}
				var fetcherIndex = columnFetcherIndexMap != null ? columnFetcherIndexMap[modelCol] : scanningResults.getFetcherIndex(fetcher.getId());
				String text = "";
				if (fetcherIndex >= 0 && fetcherIndex < values.size()) {
					var value = values.get(fetcherIndex);
					if (value != null)
						text = value.toString();
				}
				// the Opener Launch column only shows the triangle when an Opener is assigned to this row
				if (OpenerLaunchFetcher.ID.equals(fetcher.getId())) {
					var ip = scanningResult.getAddress().getHostAddress();
					text = defaultOpenerConfig.get(ip) != null ? "▶" : "";
				}
				// TableItem.setText() indexes columns in MODEL (creation) order,
				// so write to modelCol, not to the visual position c
				item.setText(modelCol, text);
				// the status icon lives in the IP column's model slot (not always 0:
				// the leading autofit button column occupies model index 0)
				if (IPFetcher.ID.equals(fetcher.getId()))
					item.setImage(modelCol, listImages[scanningResult.getType().ordinal()]);
			}
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

				// determine which visual column was double-clicked.
				// Iterate columns in visual order (getColumnOrder) so the widths match the screen layout.
				var order = getColumnOrder();
				var x = event.x;
				var col = -1;
				var colCount = getColumnCount();
				for (var vi = 0; vi < colCount; vi++) {
					var modelCol = (order != null && vi < order.length) ? order[vi] : vi;
					x -= getColumn(modelCol).getWidth();
					if (x <= 0) {
						col = vi;
						break;
					}
				}
				if (col < 0) return;

				// col is the visual position; map it to the model column
				var modelCol = modelColumnIndex(col);
				if (!(getColumn(modelCol).getData() instanceof Fetcher clickedFetcher)) return;
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
						updateOpenerAssignment(i, name);
					}
				});
			}

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
				// ignore the click while a previous Opener launch is still in progress
				if (launching.get()) return;

				var item = getItem(new Point(event.x, event.y));
				if (item == null) return;

				var row = indexOf(item);
				if (row < 0 || row >= getItemCount()) return;

				// the whole Opener Launch column is clickable.
				// Iterate columns in visual order so widths match the screen layout.
				var order = getColumnOrder();
				var x = event.x;
				var col = -1;
				var colCount = getColumnCount();
				for (var vi = 0; vi < colCount; vi++) {
					var modelCol = (order != null && vi < order.length) ? order[vi] : vi;
					x -= getColumn(modelCol).getWidth();
					if (x <= 0) {
						col = vi;
						break;
					}
				}
				if (col < 0) return;

				// col is the visual position; map it to the model column
				var modelCol = modelColumnIndex(col);
				if (!(getColumn(modelCol).getData() instanceof Fetcher clickedFetcher)) return;
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
		// only accept the click if an Opener is assigned to this row
		var openerName = defaultOpenerConfig.get(ip);
		if (openerName == null) return;

		var opener = openersConfig.getOpener(openerName);
		if (opener == null) return;

		launching.set(true);
		new Thread(() -> {
			try {
				openerLauncher.launch(opener, row);
			}
			catch (UserErrorException e) {
				final var error = e;
				getDisplay().asyncExec(() -> {
					var mb = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					mb.setText(Labels.getLabel("title.error"));
					mb.setMessage(error.getMessage());
					mb.open();
				});
			}
			catch (Exception ignore) {
				// never let an exception in the launcher thread break anything
			}
			finally {
				// keep the launch "busy" for a short while so rapid successive clicks on
				// different rows don't queue up and launch many openers at once
				try { Thread.sleep(LAUNCH_COOLDOWN_MS); } catch (InterruptedException ignored) {}
				launching.set(false);
			}
		}, "OpenerLauncher-" + row).start();
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
				// if the right-click is on the header, use the column visibility menu; otherwise restore the row menu
				if (tableLoc.y <= getHeaderHeight()) {
					if (getMenu() != columnVisibilityMenu) {
						rowContextMenu = getMenu();
						setMenu(columnVisibilityMenu);
					}
				}
				else if (rowContextMenu != null && getMenu() != rowContextMenu) {
					setMenu(rowContextMenu);
				}
				// else: leave whatever row context menu MainWindow assigned via setMenu()
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop
			}
		}
	}

	/**
	 * Polls cursor position to show a width-capped, wrapping tooltip for the hovered column header.
	 * Uses polling instead of events because SWT on Windows does not generate mouse events
	 * over the native SysHeader32 control (see PR #527).
	 */
	final class HeaderTooltipPoller implements Runnable {
		private int lastCol = -1;
		private long headerEntryTime;
		private static final int POLL_INTERVAL = 100;
		private static final int DELAY_MS = 1000;

		HeaderTooltipPoller() {
			getDisplay().timerExec(POLL_INTERVAL, this);
		}

		void dispose() {
			getDisplay().timerExec(-1, this);
			hideTooltip();
		}

		public void run() {
			if (isDisposed()) return;
			try {
				poll();
			}
			catch (Exception e) {
				// never let an exception break the SWT event loop
			}
			if (!isDisposed()) {
				getDisplay().timerExec(POLL_INTERVAL, this);
			}
		}

		private void poll() {
			var display = getDisplay();
			var parentShell = getShell();
			if (display.getActiveShell() != parentShell) {
				hideTooltip();
				lastCol = -1;
				headerEntryTime = 0;
				return;
			}
			var cursorPos = display.getCursorLocation();
			var sz = getSize();
			if (sz.x <= 0 || sz.y <= 0) return;

			var origin = toDisplay(0, 0);
			if (cursorPos.x < origin.x || cursorPos.x >= origin.x + sz.x ||
				cursorPos.y < origin.y || cursorPos.y >= origin.y + sz.y) {
				hideTooltip();
				lastCol = -1;
				headerEntryTime = 0;
				return;
			}

			var cp = toControl(cursorPos.x, cursorPos.y);
			var cy = cp.y;
			var cx = cp.x;

			var headerBottom = getHeaderHeight();
			if (headerBottom <= 0) return;

			if (cy >= headerBottom) {
				hideTooltip();
				lastCol = -1;
				headerEntryTime = 0;
				return;
			}

			if (headerEntryTime == 0) {
				headerEntryTime = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() - headerEntryTime < DELAY_MS) return;

			var col = -1;
			var colX = 0;
			var order = getColumnOrder();
			var colCount = getColumnCount();
			for (var vi = 0; vi < colCount; vi++) {
				var modelCol = (order != null && vi < order.length) ? order[vi] : vi;
				colX += getColumn(modelCol).getWidth();
				if (cx < colX) {
					col = vi;
					break;
				}
			}
			if (col < 0) {
				hideTooltip();
				lastCol = -1;
				return;
			}

			var modelCol = modelColumnIndex(col);
			var colData = getColumn(modelCol).getData();
			String info;
			if (colData instanceof Fetcher fetcher) {
				info = fetcher.getInfo();
			}
			else {
				// the leading autofit ("<->") button column has no Fetcher behind it,
				// show a hint about what clicking it does instead
				info = Labels.getLabel("table.column.autofit");
			}
			if (info == null || info.isEmpty()) {
				hideTooltip();
				lastCol = -1;
				return;
			}

			var loc = toDisplay(cx, headerBottom);
			if (col != lastCol) {
				showTooltip(info, loc.x, loc.y);
				lastCol = col;
			}
			else if (tooltipShell != null && !tooltipShell.isDisposed()) {
				tooltipShell.setLocation(loc.x, loc.y);
			}
		}
	}

	/** maximum width of the header hover tooltip, in pixels */
	private static final int TOOLTIP_MAX_WIDTH = 360;

	private void showTooltip(String text, int displayX, int displayY) {
		if (tooltipShell == null || tooltipShell.isDisposed()) {
			tooltipShell = new Shell(getShell(), SWT.ON_TOP | SWT.TOOL | SWT.NO_FOCUS);
			tooltipShell.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			var layout = new GridLayout(1, false);
			layout.marginWidth = 4;
			layout.marginHeight = 3;
			tooltipShell.setLayout(layout);

			tooltipLabel = new Text(tooltipShell, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
			tooltipLabel.setEditable(false);
			tooltipLabel.setBackground(tooltipShell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			tooltipLabel.setForeground(tooltipShell.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		}

		// width: exactly the widest text line (plus a few pixels of slack so
		// nothing wraps unintentionally), capped at TOOLTIP_MAX_WIDTH — long
		// fetcher descriptions wrap as before, short hints (e.g. the autofit
		// column's) shrink to fit their text
		var gc = new GC(tooltipLabel);
		var widestLine = 0;
		try {
			for (var line : text.split("\n", -1)) {
				widestLine = Math.max(widestLine, gc.textExtent(line).x);
			}
		}
		finally {
			gc.dispose();
		}
		tooltipLabel.setLayoutData(new GridData(Math.min(TOOLTIP_MAX_WIDTH, widestLine + 4), SWT.DEFAULT));

		tooltipLabel.setText(text);
		tooltipShell.pack();
		tooltipShell.setLocation(displayX, displayY);
		if (!tooltipShell.isVisible()) {
			tooltipShell.setVisible(true);
		}
	}

	private void hideTooltip() {
		if (tooltipShell != null && !tooltipShell.isDisposed() && tooltipShell.isVisible()) {
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
				// the autofit button column is never movable
				if (col != null && col.getData() instanceof Fetcher) col.setMoveable(moveable);
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
