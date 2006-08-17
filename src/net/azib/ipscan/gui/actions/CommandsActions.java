/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.DetailsWindow;
import net.azib.ipscan.gui.EditOpenersDialog;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.SWT;
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
		ResultTable resultTable;
		
		public Details(ResultTable table) {
			resultTable = table;
		}

		public void handleEvent(Event event) {
			checkSelection(resultTable);
			new DetailsWindow(resultTable).open(); 
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
		
		public SelectOpener(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String name = menuItem.getText();
			int indexOf = name.lastIndexOf('\t');
			if (indexOf >= 0) {
				name = name.substring(0, indexOf);
			}
			String openerString = Config.getOpenersConfig().get(name);
			
			int selectedItem = mainWindow.getResultTable().getSelectionIndex();
			if (selectedItem < 0) {
				throw new UserErrorException("commands.noSelection");
			}				

			openerString = prepareOpenerStringForItem(openerString, selectedItem);
			
			new OpenerLauncher().launch(openerString);
		}
		
		/**
		 * Replaces references to scanned values in an opener string.
		 * Refefernces look like ${fetcher_label}
		 * @param openerString
		 * @return opener string with values replaced
		 */
		String prepareOpenerStringForItem(String openerString, int selectedItem) {
			Pattern paramsPattern = Pattern.compile("\\$\\{(.+?)\\}");
			Matcher matcher = paramsPattern.matcher(openerString);
			StringBuffer sb = new StringBuffer(64);
			while (matcher.find()) {
				// resolve the required fetcher
				String fetcherName = matcher.group(1);
				int fetcherIndex = FetcherRegistry.getInstance().getSelectedFetcherIndex(fetcherName);
				if (fetcherIndex < 0) {
					throw new UserErrorException("opener.unknownFetcher", fetcherName);
				}

				// retrieve the scanned value
				String scannedValue = getScannedValue(selectedItem, fetcherIndex);
				if (scannedValue == null) {
					throw new UserErrorException("opener.nullFetcherValue", fetcherName);					
				}
				
				matcher.appendReplacement(sb, scannedValue);
			}
			matcher.appendTail(sb);
			return sb.toString();
		}

		String getScannedValue(int selectedItem, int fetcherIndex) {
			return (String) mainWindow.getResultTable().getScanningResults().getResult(selectedItem).getValues().get(fetcherIndex);
		}
		
	}

}
