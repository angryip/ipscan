/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsActions;
import net.azib.ipscan.gui.actions.FavoritesActions;
import net.azib.ipscan.gui.actions.FileActions;
import net.azib.ipscan.gui.actions.GotoActions;
import net.azib.ipscan.gui.actions.HelpActions;
import net.azib.ipscan.gui.actions.ToolsActions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * MainMenu
 *
 * @author anton
 */
public class MainMenu {
	
	private MutablePicoContainer container;
	
	public MainMenu(Shell shell, Menu mainMenu, CommandsMenu resultsContextMenu, PicoContainer parentContainer) {
		
		// create the menu-specific child container
		container = new DefaultPicoContainer(parentContainer);
		
		// register some components not registered in the main menu
		container.registerComponentImplementation(FavoritesMenu.class);
		container.registerComponentImplementation(FavoritesActions.ShowMenu.class);
		container.registerComponentImplementation(FavoritesActions.Select.class);		
		container.registerComponentImplementation(FavoritesActions.Add.class);		
		container.registerComponentImplementation(FavoritesActions.Edit.class);
		
		container.registerComponentImplementation(CommandsActions.EditOpeners.class);
		container.registerComponentImplementation(CommandsActions.SelectOpener.class);
		container.registerComponentImplementation(CommandsActions.ShowOpenersMenu.class);		
		// this one is not cached because we need 2 instances of it - in the Commands menu and in the context menu
		container.registerComponent(new ConstructorInjectionComponentAdapter(OpenersMenu.class, OpenersMenu.class)); 
		
		shell.setMenuBar(mainMenu);		
		createMainMenuItems(mainMenu);
				
		createCommandsMenuItems(resultsContextMenu);
	}

	private void createMainMenuItems(Menu menu) {
		
		Menu subMenu = initMenu(menu, "menu.file");
		initMenuItem(subMenu, "menu.file.saveAll", new Integer(SWT.CONTROL | 'S'), initListener(FileActions.SaveAll.class));
		initMenuItem(subMenu, "menu.file.saveSelection", null, initListener(FileActions.SaveSelection.class));
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.file.exportOptions", null, null);
		initMenuItem(subMenu, "menu.file.importOptions", null, null);
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.file.exit", null, initListener(FileActions.Exit.class));
		
		subMenu = initMenu(menu, "menu.goto");
		initMenuItem(subMenu, "menu.goto.aliveHost", new Integer(SWT.CONTROL | SWT.SHIFT | 'H'), initListener(GotoActions.NextAliveHost.class));
		initMenuItem(subMenu, "menu.goto.deadHost", new Integer(SWT.CONTROL | SWT.SHIFT | 'D'), initListener(GotoActions.NextDeadHost.class));
		initMenuItem(subMenu, "menu.goto.openPort", new Integer(SWT.CONTROL | SWT.SHIFT | 'P'), initListener(GotoActions.NextHostWithInfo.class));
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.goto.find", new Integer(SWT.CONTROL | 'F'), initListener(GotoActions.Find.class));
		
		subMenu = initMenu(menu, "menu.commands");
		createCommandsMenuItems(subMenu);

		createFavoritesMenu(menu);
		
		subMenu = initMenu(menu, "menu.tools");
		initMenuItem(subMenu, "menu.tools.options", new Integer(SWT.CONTROL | 'O'), initListener(ToolsActions.Options.class));
		initMenuItem(subMenu, "menu.tools.fetchers", null, null);
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.tools.delete", null, null);
		initMenuItem(subMenu, "menu.tools.lastInfo", new Integer(SWT.CONTROL | 'I'), null);
		
		subMenu = initMenu(menu, "menu.help");
		initMenuItem(subMenu, "menu.help.gettingStarted", new Integer(SWT.F1), initListener(HelpActions.GettingStarted.class));
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.help.website", null, initListener(HelpActions.Website.class));
		initMenuItem(subMenu, "menu.help.forum", null, initListener(HelpActions.Forum.class));
		initMenuItem(subMenu, "menu.help.plugins", null, initListener(HelpActions.Plugins.class));
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.help.cmdLine", null, null);
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.help.checkVersion", null, initListener(HelpActions.CheckVersion.class));
		initMenuItem(subMenu, null, null, null);
		initMenuItem(subMenu, "menu.help.about", new Integer(SWT.F12), initListener(HelpActions.About.class));
	}

	private void createCommandsMenuItems(Menu menu) {
		initMenuItem(menu, "menu.commands.details", null, initListener(CommandsActions.Details.class));
		initMenuItem(menu, null, null, null);
		initMenuItem(menu, "menu.commands.rescan", new Integer(SWT.CONTROL | 'R'), null);
		initMenuItem(menu, "menu.commands.delete", new Integer(SWT.DEL), initListener(CommandsActions.Delete.class));
		initMenuItem(menu, null, null, null);
		initMenuItem(menu, "menu.commands.copy", new Integer(SWT.CONTROL | 'C'), initListener(CommandsActions.CopyIP.class));
		initMenuItem(menu, "menu.commands.copyDetails", null, initListener(CommandsActions.CopyIPDetails.class));
		initMenuItem(menu, null, null, null);		
		createOpenersMenu(menu);
		// initMenuItem(subMenu, "menu.commands.show", null, initListener());
	}

	private void createOpenersMenu(Menu subMenu) {
		OpenersMenu openersMenu = (OpenersMenu) container.getComponentInstance(OpenersMenu.class);
		MenuItem openersMenuItem = new MenuItem(subMenu, SWT.CASCADE);
		openersMenuItem.setText(Labels.getLabel("menu.commands.open"));
		openersMenuItem.setMenu(openersMenu);
	}

	private void createFavoritesMenu(Menu parentMenu) {
		MenuItem favoritesMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
		favoritesMenuItem.setText(Labels.getLabel("menu.favorites"));
		Menu favoritesMenu = (Menu) container.getComponentInstance(FavoritesMenu.class);
		favoritesMenuItem.setMenu(favoritesMenu);
	}

	private static Menu initMenu(Menu menu, String label) {
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Labels.getLabel(label));
		
		Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
		menuItem.setMenu(subMenu);
		
		return subMenu;
	}
	
	private Listener initListener(Class listenerClass) {
		// register the component if it is not registered yet
		if (container.getComponentAdapter(listenerClass) == null)
			container.registerComponentImplementation(listenerClass);
		// .. and create the instance, satisfying all the dependencies
		return (Listener) container.getComponentInstance(listenerClass);
	}
	
	private static MenuItem initMenuItem(Menu parent, String label, Integer accelerator, Listener listener) {
		MenuItem menuItem = new MenuItem(parent, label == null ? SWT.SEPARATOR : SWT.PUSH);
		
		if (label != null) 
			menuItem.setText(Labels.getLabel(label));
		
		if (accelerator != null)
			menuItem.setAccelerator(accelerator.intValue());
		
		if (listener != null)
			menuItem.addListener(SWT.Selection, listener);
		else
			menuItem.setEnabled(false);
		
		return menuItem;
	}
			
	/**
	 * CommandsMenu wrapper for type-safety
	 */
	public static class CommandsMenu extends Menu {
		public CommandsMenu(Decorations parent) {
			super(parent, SWT.POP_UP);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * OpenersMenu wrapper for type-safety
	 */
	public static class OpenersMenu extends Menu {
		public OpenersMenu(Decorations parent, CommandsActions.EditOpeners editOpenersListener, CommandsActions.ShowOpenersMenu showOpenersMenuListener) {
			super(parent, SWT.DROP_DOWN);

			initMenuItem(this, "menu.commands.open.edit", null, editOpenersListener);
			initMenuItem(this, null, null, null);
			
			addListener(SWT.Show, showOpenersMenuListener);

			// run the listener to populate the menu initially and initialize accelerators
			Event e = new Event();
			e.widget = this;
			showOpenersMenuListener.handleEvent(e);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}

	/**
	 * FavoritesMenu wrapper for type-safety
	 */
	public static class FavoritesMenu extends Menu {
		public FavoritesMenu(Decorations parent, FavoritesActions.Add addListener, FavoritesActions.Edit editListener, FavoritesActions.ShowMenu showFavoritesMenuListener) {
			super(parent, SWT.DROP_DOWN);

			initMenuItem(this, "menu.favorites.add", new Integer(SWT.CONTROL | 'D'), addListener);
			initMenuItem(this, "menu.favorites.edit", null, editListener);
			initMenuItem(this, null, null, null);
			
			addListener(SWT.Show, showFavoritesMenuListener);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * ColumnsMenu wrapper for type-safety.
	 * This is the menu when clicking on a column header.
	 */
	public static class ColumnsMenu extends Menu {
		public ColumnsMenu(Decorations parent, ColumnsActions.SortBy sortByListener) {
			super(parent, SWT.POP_UP);
			
			initMenuItem(this, "menu.columns.sortBy", null, sortByListener);
			initMenuItem(this, "menu.columns.info", null, null);
			initMenuItem(this, "menu.columns.options", null, null);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
}
