/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.gui.DetailsWindow;
import net.azib.ipscan.gui.EditOpenersDialog;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ResultTable;
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
			new DetailsWindow(resultTable).open(); 
		}
	}
	
	public static class Delete implements Listener {
		private ResultTable resultTable;
		
		public Delete(ResultTable resultTable) {
			this.resultTable = resultTable;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			int firstSelection = resultTable.getSelectionIndex();
			resultTable.remove(resultTable.getSelectionIndices());
			resultTable.setSelection(firstSelection);
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
	private static void checkSelection(ResultTable resultTable) {
		if (resultTable.getItemCount() <= 0) {
			throw new UserErrorException("commands.noResults");
		}
		else
		if (resultTable.getSelectionIndex() < 0) {
			throw new UserErrorException("commands.noSelection");
		}
	}
	
	public static class ShowOpenersMenu implements Listener {
		
		private Menu openersMenu;
		private Listener openersSelectListener;

		public ShowOpenersMenu(MainWindow mainWindow, Menu openersMenu) {
			this.openersMenu = openersMenu;
			this.openersSelectListener = new SelectOpener(mainWindow);
		}

		public void handleEvent(Event event) {
			MenuItem[] menuItems = openersMenu.getItems();
			for (int i = 2; i < menuItems.length; i++) {
				menuItems[i].dispose();
			}
			
			// update menu items
			int index = 0;
			for (Iterator i = Config.getOpenersConfig().iterateNames(); i.hasNext();) {
				MenuItem menuItem = new MenuItem(openersMenu, SWT.CASCADE);
				String name = (String)i.next();
				
				index++;
				if (index <= 9) {
					name += "\tCtrl+" + index;
					menuItem.setAccelerator(SWT.CONTROL | ('0' + index));
				}
				
				menuItem.setText(name);
				menuItem.setData(new Integer(index));
				menuItem.addListener(SWT.Selection, openersSelectListener);
			}
			

		}

	}
	
	public static class EditOpeners implements Listener {

		public void handleEvent(Event event) {
			new EditOpenersDialog().open(); 
		}
	}
	
	public static class SelectOpener implements Listener {
		
		private MainWindow mainWindow;
		private OpenerLauncher openerLauncher;
		
		public SelectOpener(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
			this.openerLauncher = new OpenerLauncher(mainWindow);
		}
		
		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String name = menuItem.getText();
			int indexOf = name.lastIndexOf('\t');
			if (indexOf >= 0) {
				name = name.substring(0, indexOf);
			}
			Opener opener = Config.getOpenersConfig().getOpener(name);
			
			int selectedItem = mainWindow.getResultTable().getSelectionIndex();
			if (selectedItem < 0) {
				throw new UserErrorException("commands.noSelection");
			}				
					
			try {
				mainWindow.setStatusText(Labels.getInstance().getString("state.opening") + name);
				openerLauncher.launch(opener, selectedItem);
				// wait a bit to make status visible
				// TODO: somehow wait until the process is started
				Thread.sleep(500);
			}
			catch (InterruptedException e) {}
			finally {
				mainWindow.setStatusText(null);
			}
		}
	}

}
