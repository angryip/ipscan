/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.FavoritesConfig;
import net.azib.ipscan.config.Labels;
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

	public static final class Add implements Listener {
		private final FeederGUIRegistry feederRegistry;
		private final FavoritesConfig favoritesConfig;
		
		public Add(FavoritesConfig favoritesConfig, FeederGUIRegistry feederRegistry) {
			this.favoritesConfig = favoritesConfig;
			this.feederRegistry = feederRegistry;
		}
		
		public void handleEvent(Event event) {
			String feederInfo = feederRegistry.current().getInfo();
			InputDialog inputDialog = new InputDialog(
					Labels.getLabel("title.favorite.add"), 
					Labels.getLabel("text.favorite.add"));
			String favoriteName = inputDialog.open(feederInfo);
			
			if (favoriteName != null) {
				if (favoritesConfig.get(favoriteName) != null) {
					throw new UserErrorException("favorite.alreadyExists");
				}
				String serializedFeeder = feederRegistry.current().getFeederName() + '\t' + feederRegistry.current().serialize();				
				favoritesConfig.add(favoriteName, serializedFeeder);
				event.display.getActiveShell().setText(favoriteName + " - " + Version.NAME);
			}
		}
	}
	
	public static final class Select implements Listener {
		private final FeederGUIRegistry feederRegistry;
		private final FavoritesConfig favoritesConfig;
		
		public Select(FavoritesConfig favoritesConfig, FeederGUIRegistry feederRegistry) {
			this.favoritesConfig = favoritesConfig;
			this.feederRegistry = feederRegistry;
		}

		public void handleEvent(Event event) {
			MenuItem menuItem = (MenuItem) event.widget;
			String serializedFeeder = favoritesConfig.get(menuItem.getText());
			
			int indexOf = serializedFeeder.indexOf('\t');
			String feederName = serializedFeeder.substring(0, indexOf);
			serializedFeeder = serializedFeeder.substring(indexOf + 1);
			
			feederRegistry.select(feederName);
			feederRegistry.current().unserialize(serializedFeeder);
			event.display.getActiveShell().setText(menuItem.getText() + " - " + Version.NAME);
		}
	}
	
	public static final class Edit implements Listener {
		private final FavoritesConfig favoritesConfig;
		
		public Edit(FavoritesConfig favoritesConfig) {
			this.favoritesConfig = favoritesConfig;
		}

		public void handleEvent(Event event) {
			new EditFavoritesDialog(favoritesConfig).open();
		}
	}

	public static final class ShowMenu implements Listener {
		private final Listener favoritesSelectListener;
		private final FavoritesConfig favoritesConfig;
		
		public ShowMenu(FavoritesConfig favoritesConfig, Select favoritesSelectListener) {
			this.favoritesConfig = favoritesConfig;
			// the listener for favorites selections from the menu
			this.favoritesSelectListener = favoritesSelectListener;
		}

		public void handleEvent(Event event) {
			Menu favoritesMenu = (Menu) event.widget;
			// populate favorites in the menu			
			// note: 3 is the number of items in the menu when no favorites exist
			
			// dispose old favorites
			MenuItem[] menuItems = favoritesMenu.getItems();
			for (int i = 3; i < menuItems.length; i++) {
				menuItems[i].dispose();
			}
			
			// update favorites menu items
			for (String name : favoritesConfig) {
				MenuItem menuItem = new MenuItem(favoritesMenu, SWT.CASCADE);
				menuItem.setText(name);
				menuItem.addListener(SWT.Selection, favoritesSelectListener);
			}
		}
	}

}
