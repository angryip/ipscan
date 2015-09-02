/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.DetailsWindow;
import net.azib.ipscan.gui.EditOpenersDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Commands and Context menu Actions.
 * All these operate on the items, selected in the results list.
 *
 * @author Anton Keks
 */
@Singleton
public class CommandsMenuActions {
	@Inject public Details details;
	@Inject public Delete delete;
	@Inject public Rescan rescan;
	@Inject public CopyIP copyIP;
	@Inject public CopyIPDetails copyIPDetails;
	@Inject public ShowOpenersMenu showOpenersMenu;
	@Inject public EditOpeners editOpeners;
	@Inject public SelectOpener selectOpener;

	@Inject public CommandsMenuActions() {}

	/**
	 * Checks that there is at least one item selected in the results list.
	 */
	static void checkSelection(ResultTable resultTable) {
		if (resultTable.getItemCount() <= 0) {
			throw new UserErrorException("commands.noResults");
		}
		else
		if (resultTable.getSelectionIndex() < 0) {
			throw new UserErrorException("commands.noSelection");
		}
	}

	public static class Details implements Listener {
		private final ResultTable resultTable;
		private final DetailsWindow detailsWindow;
		
		@Inject public Details(ResultTable resultTable, DetailsWindow detailsWindow) {
			this.resultTable = resultTable;
			this.detailsWindow = detailsWindow;
			resultTable.addListener(SWT.Traverse, this);
			resultTable.addListener(SWT.MouseDoubleClick, this);
		}

		public void handleEvent(Event event) {
			// activate only if something is selected
			if (event.type == SWT.Selection || (resultTable.getSelectionIndex() >= 0 && (event.type == SWT.MouseDoubleClick || event.detail == SWT.TRAVERSE_RETURN))) {
				event.doit = false;
				checkSelection(resultTable);
				detailsWindow.open(); 
			}
		}
	}
	
	public static final class Delete implements Listener {
		private final ResultTable resultTable;
		private final StateMachine stateMachine;

		@Inject public Delete(ResultTable resultTable, StateMachine stateMachine) {
			this.resultTable = resultTable;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event event) {
			// ignore other keys if this is a KeyDown event - 
			// the same listener is used for several events
			if (event.type == SWT.KeyDown && event.keyCode != SWT.DEL) return;
			// deletion not allowed when scanning
			if (!stateMachine.inState(ScanningState.IDLE)) return;
			
			int firstSelection = resultTable.getSelectionIndex();
			if (firstSelection < 0) return;

			resultTable.remove(resultTable.getSelectionIndices());
			resultTable.setSelection(firstSelection);
			event.widget = resultTable;
			resultTable.notifyListeners(SWT.Selection, event);
		}
	}

	public static final class Rescan implements Listener {
		private final ResultTable resultTable;
		private final StateMachine stateMachine;

		@Inject
		public Rescan(ResultTable resultTable, StateMachine stateMachine) {
			this.resultTable = resultTable;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			stateMachine.rescan();
		}
	}
	
	/**
	 * Copies currently selected IP to the clipboard.
	 * Used as both menu item listener and key down listener.
	 */
	public static final class CopyIP implements Listener {
		private final ResultTable resultTable;

		@Inject
		public CopyIP(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			if (event.type == SWT.KeyDown) {
				// if this is not Ctrl+C or nothing is selected, then simply do nothing
				if ((event.keyCode != 'c' && event.stateMask != SWT.MOD1) || resultTable.getSelectionIndex() < 0)
					return;
			}
			else {
				// if selected from the menu, check selection
				checkSelection(resultTable);
			}
			Clipboard clipboard = new Clipboard(event.display);
			clipboard.setContents(new Object[] {resultTable.getItem(resultTable.getSelectionIndex()).getText()}, new Transfer[] {TextTransfer.getInstance()});
			clipboard.dispose();
		}
	}
	
	public static final class CopyIPDetails implements Listener {
		private final ResultTable resultTable;

		@Inject
		public CopyIPDetails(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			Clipboard clipboard = new Clipboard(event.display);
			clipboard.setContents(new Object[] {resultTable.getSelectedResult().toString()}, new Transfer[] {TextTransfer.getInstance()});
			clipboard.dispose();
		}
	}
	
	public static final class ShowOpenersMenu implements Listener {
		
		private final Listener openersSelectListener;
		private final OpenersConfig openersConfig;

		@Inject
		public ShowOpenersMenu(OpenersConfig openersConfig, SelectOpener selectOpener) {
			this.openersConfig = openersConfig;
			this.openersSelectListener = selectOpener;
		}

		public void handleEvent(Event event) {
			Menu openersMenu = (Menu)event.widget;
			MenuItem[] menuItems = openersMenu.getItems();
			for (int i = 2; i < menuItems.length; i++) {
				menuItems[i].dispose();
			}
			
			// update menu items
			int index = 0;
			for (String name : openersConfig) {
				MenuItem menuItem = new MenuItem(openersMenu, SWT.CASCADE);
				
				index++;
				if (index <= 9) {
					name += "\tCtrl+" + index;
					menuItem.setAccelerator(SWT.MOD1 | ('0' + index));
				}
				
				menuItem.setText(name);
				menuItem.setData(index);
				menuItem.addListener(SWT.Selection, openersSelectListener);
			}

		}
	}
		
	public static final class EditOpeners implements Listener {
		
		private final FetcherRegistry fetcherRegistry;
		private final OpenersConfig openersConfig;

		@Inject
		public EditOpeners(FetcherRegistry fetcherRegistry, OpenersConfig openersConfig) {
			this.fetcherRegistry = fetcherRegistry;
			this.openersConfig = openersConfig;
		}

		public void handleEvent(Event event) {
			new EditOpenersDialog(fetcherRegistry, openersConfig).open(); 
		}
	}
	
	public static final class SelectOpener implements Listener {
		
		private final StatusBar statusBar;
		private final ResultTable resultTable;
		private final OpenerLauncher openerLauncher;
		private final OpenersConfig openersConfig;

		@Inject
		public SelectOpener(OpenersConfig openersConfig, StatusBar statusBar, ResultTable resultTable, OpenerLauncher openerLauncher) {
			this.openersConfig = openersConfig;
			this.statusBar = statusBar;
			this.resultTable = resultTable;
			this.openerLauncher = openerLauncher;
		}
		
		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String name = menuItem.getText();
			int indexOf = name.lastIndexOf('\t');
			if (indexOf >= 0) {
				name = name.substring(0, indexOf);
			}
			Opener opener = openersConfig.getOpener(name);

			int[] selectionIndices = resultTable.getSelectionIndices();
			if (selectionIndices.length == 0)
				throw new UserErrorException("commands.noSelection");

			for (int i : selectionIndices) {
				try {
					statusBar.setStatusText(Labels.getLabel("state.opening") + name);
					openerLauncher.launch(opener, i);
					// wait a bit to make status visible
					Thread.sleep(100);
				}
				catch (InterruptedException ignore) {}
				finally {
					statusBar.setStatusText(null);
				}
			}
		}
	}
}
