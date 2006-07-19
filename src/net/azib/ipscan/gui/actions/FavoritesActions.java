/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.FavoritesConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.EditFavoritesDialog;
import net.azib.ipscan.gui.InputDialog;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * FavoritesActions
 *
 * @author anton
 */
public class FavoritesActions {

	public static class Add implements Listener {
		private MainWindow mainWindow;
		
		public Add(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		public void handleEvent(Event event) {
			String feederInfo = mainWindow.getFeederGUI().getInfo();
			InputDialog inputDialog = new InputDialog(
					Labels.getInstance().getString("title.favorite.add"), 
					Labels.getInstance().getString("text.favorite.add"));
			inputDialog.setText(feederInfo);
			String favoriteName = inputDialog.open();
			
			if (favoriteName != null) {
				FavoritesConfig favoritesConfig = Config.getFavoritesConfig();
				if (favoritesConfig.get(favoriteName) != null) {
					throw new UserErrorException("favorite.alreadyExists");
				}
				String serializedFeeder = mainWindow.getFeederGUI().getFeederName() + '\t' + mainWindow.getFeederGUI().serialize();				
				favoritesConfig.add(favoriteName, serializedFeeder);
			}
		}
	}
	
	public static class Select implements Listener {
		private MainWindow mainWindow;
		
		public Select(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String serializedFeeder = Config.getFavoritesConfig().get(menuItem.getText());
			
			int indexOf = serializedFeeder.indexOf('\t');
			String feederName = serializedFeeder.substring(0, indexOf);
			serializedFeeder = serializedFeeder.substring(indexOf + 1);
			
			mainWindow.selectFeederGUI(feederName);
			mainWindow.getFeederGUI().unserialize(serializedFeeder);
		}
	}
	
	public static class Edit implements Listener {
		public void handleEvent(Event event) {
			new EditFavoritesDialog();
		}
	}

	public static class ShowMenu implements Listener {
		private Menu favoritesMenu;
		private Listener favoritesSelectListener;
		
		public ShowMenu(MainWindow mainWindow, Menu favoritesMenu) {
			this.favoritesMenu = favoritesMenu;
			// the listener for favorites selections from the menu
			this.favoritesSelectListener = new Select(mainWindow);
		}

		public void handleEvent(Event event) {
			// populate favorites in the menu
			FavoritesConfig favoritesConfig = Config.getFavoritesConfig();
			
			// note: 3 is the number of items in the menu when no favorites exist
			// dispose old favorites
			MenuItem[] menuItems = favoritesMenu.getItems();
			for (int i = 3; i < menuItems.length; i++) {
				menuItems[i].dispose();
			}
			
			// update favorites menu items
			for (Iterator i = favoritesConfig.iterateNames(); i.hasNext();) {
				MenuItem menuItem = new MenuItem(favoritesMenu, SWT.CASCADE);
				menuItem.setText((String) i.next());
				menuItem.addListener(SWT.Selection, favoritesSelectListener);
			}
		}
	}

}
