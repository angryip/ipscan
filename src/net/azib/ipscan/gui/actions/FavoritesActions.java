/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.NamedListConfig;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.gui.EditFavoritesDialog;
import net.azib.ipscan.gui.InputDialog;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * FavoritesActions
 *
 * @author Anton Keks
 */
public class FavoritesActions {

	public static class Add implements Listener {
		private FeederGUIRegistry feederRegistry;
		
		public Add(FeederGUIRegistry feederRegistry) {
			this.feederRegistry = feederRegistry;
		}
		
		public void handleEvent(Event event) {
			String feederInfo = feederRegistry.current().getInfo();
			InputDialog inputDialog = new InputDialog(
					Labels.getLabel("title.favorite.add"), 
					Labels.getLabel("text.favorite.add"));
			String favoriteName = inputDialog.open(feederInfo);
			
			if (favoriteName != null) {
				NamedListConfig favoritesConfig = Config.getFavoritesConfig();
				if (favoritesConfig.get(favoriteName) != null) {
					throw new UserErrorException("favorite.alreadyExists");
				}
				String serializedFeeder = feederRegistry.current().getFeederName() + '\t' + feederRegistry.current().serialize();				
				favoritesConfig.add(favoriteName, serializedFeeder);
				event.display.getActiveShell().setText(favoriteName + " - " + Version.NAME);
			}
		}
	}
	
	public static class Select implements Listener {
		private FeederGUIRegistry feederRegistry;
		
		public Select(FeederGUIRegistry feederRegistry) {
			this.feederRegistry = feederRegistry;
		}

		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String serializedFeeder = Config.getFavoritesConfig().get(menuItem.getText());
			
			int indexOf = serializedFeeder.indexOf('\t');
			String feederName = serializedFeeder.substring(0, indexOf);
			serializedFeeder = serializedFeeder.substring(indexOf + 1);
			
			feederRegistry.select(feederName);
			feederRegistry.current().unserialize(serializedFeeder);
			event.display.getActiveShell().setText(menuItem.getText() + " - " + Version.NAME);
		}
	}
	
	public static class Edit implements Listener {
		public void handleEvent(Event event) {
			new EditFavoritesDialog().open();
		}
	}

	public static class ShowMenu implements Listener {
		private Listener favoritesSelectListener;
		
		public ShowMenu(Select favoritesSelectListener) {
			// the listener for favorites selections from the menu
			this.favoritesSelectListener = favoritesSelectListener;
		}

		public void handleEvent(Event event) {
			Menu favoritesMenu = (Menu) event.widget;
			// populate favorites in the menu
			NamedListConfig favoritesConfig = Config.getFavoritesConfig();
			
			// note: 3 is the number of items in the menu when no favorites exist
			// dispose old favorites
			MenuItem[] menuItems = favoritesMenu.getItems();
			for (int i = 3; i < menuItems.length; i++) {
				menuItems[i].dispose();
			}
			
			// update favorites menu items
			for (Iterator<String> i = favoritesConfig.iterateNames(); i.hasNext();) {
				MenuItem menuItem = new MenuItem(favoritesMenu, SWT.CASCADE);
				menuItem.setText(i.next());
				menuItem.addListener(SWT.Selection, favoritesSelectListener);
			}
		}
	}

}
