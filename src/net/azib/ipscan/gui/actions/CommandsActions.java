/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.DetailsDialog;
import net.azib.ipscan.gui.EditOpenersDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Commands and Context menu Actions.
 * All these operate on the items, selected in the results list.
 * TODO: check for selection everywhere
 *
 * @author anton
 */
public class CommandsActions {
	
	public static class Details implements Listener {
		private ResultTable resultTable;
		
		public Details(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			new DetailsDialog(resultTable).open(); 
		}
	}
	
	public static class Delete implements Listener {
		private ResultTable resultTable;
		
		public Delete(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			// ignore other keys if this is a KeyDown event - 
			// the same listener is used for several events
			if (event.type == SWT.KeyDown && event.keyCode != SWT.DEL)
				return;			
			checkSelection(resultTable);
			int firstSelection = resultTable.getSelectionIndex();
			resultTable.remove(resultTable.getSelectionIndices());
			resultTable.setSelection(firstSelection);
		}
	}
	
	public static class Rescan implements Listener {
		private ResultTable resultTable;
		private StateMachine stateMachine;
		
		public Rescan(ResultTable resultTable, StateMachine stateMachine) {
			this.resultTable = resultTable;
			this.stateMachine = stateMachine;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			stateMachine.rescan();
		}
	}
	
	public static class CopyIP implements Listener {
		private ResultTable resultTable;
		
		public CopyIP(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			Clipboard clipboard = new Clipboard(event.display);
			clipboard.setContents(new Object[] {resultTable.getItem(resultTable.getSelectionIndex()).getText()}, new Transfer[] {TextTransfer.getInstance()});
			clipboard.dispose();
		}
	}
	
	public static class CopyIPDetails implements Listener {
		private ResultTable resultTable;
		
		public CopyIPDetails(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			Clipboard clipboard = new Clipboard(event.display);
			clipboard.setContents(new Object[] {resultTable.getIPDetails()}, new Transfer[] {TextTransfer.getInstance()});
			clipboard.dispose();
		}
	}

	/**
	 * Checks that there is at least one item selected in the results list.
	 * @param mainWindow
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
	
	public static class ShowOpenersMenu implements Listener {
		
		private Listener openersSelectListener;

		public ShowOpenersMenu(SelectOpener selectOpener) {
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
			for (Iterator<String> i = Config.getOpenersConfig().iterateNames(); i.hasNext();) {
				MenuItem menuItem = new MenuItem(openersMenu, SWT.CASCADE);
				String name = i.next();
				
				index++;
				if (index <= 9) {
					name += "\tCtrl+" + index;
					menuItem.setAccelerator(SWT.MOD1 | ('0' + index));
				}
				
				menuItem.setText(name);
				menuItem.setData(new Integer(index));
				menuItem.addListener(SWT.Selection, openersSelectListener);
			}

		}
	}
	
	public static class EditOpeners implements Listener {
		
		FetcherRegistry fetcherRegistry;

		public EditOpeners(FetcherRegistry fetcherRegistry) {
			this.fetcherRegistry = fetcherRegistry;
		}

		public void handleEvent(Event event) {
			new EditOpenersDialog(fetcherRegistry).open(); 
		}
	}
	
	public static class SelectOpener implements Listener {
		
		private StatusBar statusBar;
		private ResultTable resultTable;
		private OpenerLauncher openerLauncher;
		
		public SelectOpener(StatusBar statusBar, ResultTable resultTable, OpenerLauncher openerLauncher) {
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
			Opener opener = Config.getOpenersConfig().getOpener(name);
			
			int selectedItem = resultTable.getSelectionIndex();
			if (selectedItem < 0) {
				throw new UserErrorException("commands.noSelection");
			}				
					
			try {
				statusBar.setStatusText(Labels.getLabel("state.opening") + name);
				openerLauncher.launch(opener, selectedItem);
				// wait a bit to make status visible
				// TODO: somehow wait until the process is started
				Thread.sleep(500);
			}
			catch (InterruptedException e) {}
			finally {
				statusBar.setStatusText(null);
			}
		}
	}

}
