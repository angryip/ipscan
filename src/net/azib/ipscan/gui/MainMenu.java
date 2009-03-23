/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import net.azib.ipscan.gui.actions.FavoritesMenuActions;
import net.azib.ipscan.gui.actions.ScanMenuActions;
import net.azib.ipscan.gui.actions.GotoMenuActions;
import net.azib.ipscan.gui.actions.HelpMenuActions;
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
import org.picocontainer.Startable;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * MainMenu
 *
 * @author Anton Keks
 */
public class MainMenu implements Startable {
	
	private MutablePicoContainer container;
	
	public MainMenu(Shell shell, Menu mainMenu, CommandsMenu resultsContextMenu, StateMachine stateMachine, PicoContainer parentContainer) {
		
		// create the menu-specific child container
		container = new DefaultPicoContainer(parentContainer);
		
		// register some components not registered in the main menu
		container.registerComponentImplementation(FavoritesMenu.class);
		container.registerComponentImplementation(FavoritesMenuActions.ShowMenu.class);
		container.registerComponentImplementation(FavoritesMenuActions.Select.class);		
		container.registerComponentImplementation(FavoritesMenuActions.Add.class);		
		container.registerComponentImplementation(FavoritesMenuActions.Edit.class);
		
		container.registerComponentImplementation(CommandsMenuActions.EditOpeners.class);
		container.registerComponentImplementation(CommandsMenuActions.SelectOpener.class);
		container.registerComponentImplementation(CommandsMenuActions.ShowOpenersMenu.class);
		// this one is not cached because we need 2 instances of it - in the Commands menu and in the context menu
		container.registerComponent(new ConstructorInjectionComponentAdapter(OpenersMenu.class, OpenersMenu.class)); 
		
		shell.setMenuBar(mainMenu);		
		createMainMenuItems(mainMenu);
				
		createCommandsMenuItems(resultsContextMenu);
		
		stateMachine.addTransitionListener(new MenuEnablerDisabler(mainMenu));
		stateMachine.addTransitionListener(new MenuEnablerDisabler(resultsContextMenu));
	}

	public void start() {
		// constructor starts everything
	}

	public void stop() {
	}

	private void createMainMenuItems(Menu menu) {
		
		Menu subMenu = initMenu(menu, "menu.scan");
		initMenuItem(subMenu, "menu.scan.exportAll", "Ctrl+S", new Integer(SWT.MOD1 | 'S'), initListener(ScanMenuActions.SaveAll.class), false);
		initMenuItem(subMenu, "menu.scan.exportSelection", null, null, initListener(ScanMenuActions.SaveSelection.class), false);
		initMenuItem(subMenu, null, null, null, null);
		if (!Platform.MAC_OS) {
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.scan.quit", "Ctrl+Q", new Integer(SWT.MOD1 | 'Q'), initListener(ScanMenuActions.Quit.class));
		}
		
		subMenu = initMenu(menu, "menu.goto");
		initMenuItem(subMenu, "menu.goto.next.aliveHost", "Ctrl+H", new Integer(SWT.MOD1 | 'H'), initListener(GotoMenuActions.NextAliveHost.class));
		initMenuItem(subMenu, "menu.goto.next.openPort", "Ctrl+J", new Integer(SWT.MOD1 | 'J'), initListener(GotoMenuActions.NextHostWithInfo.class));
		initMenuItem(subMenu, "menu.goto.next.deadHost", "Ctrl+K", new Integer(SWT.MOD1 | 'K'), initListener(GotoMenuActions.NextDeadHost.class));
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.goto.prev.aliveHost", "Ctrl+Shift+H", new Integer(SWT.MOD1 | SWT.MOD2 | 'H'), initListener(GotoMenuActions.PrevAliveHost.class));
		initMenuItem(subMenu, "menu.goto.prev.openPort", "Ctrl+Shift+J", new Integer(SWT.MOD1 | SWT.MOD2 | 'J'), initListener(GotoMenuActions.PrevHostWithInfo.class));
		initMenuItem(subMenu, "menu.goto.prev.deadHost", "Ctrl+Shift+K", new Integer(SWT.MOD1 | SWT.MOD2 | 'K'), initListener(GotoMenuActions.PrevDeadHost.class));
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.goto.find", "Ctrl+F", new Integer(SWT.MOD1 | 'F'), initListener(GotoMenuActions.Find.class));
		
		subMenu = initMenu(menu, "menu.commands");
		createCommandsMenuItems(subMenu);

		createFavoritesMenu(menu);
		
		subMenu = initMenu(menu, "menu.tools");
		initMenuItem(subMenu, "menu.tools.preferences", "Ctrl+O", new Integer(SWT.MOD1 | (Platform.MAC_OS ? ',' : 'O')), initListener(ToolsActions.Preferences.class), true);
		initMenuItem(subMenu, "menu.tools.fetchers", "Ctrl+Shift+O", new Integer(SWT.MOD1 | SWT.MOD2 | (Platform.MAC_OS ? ',' : 'O')), initListener(ToolsActions.ChooseFetchers.class), true);
		initMenuItem(subMenu, null, null, null, null);
		Menu selectMenu = initMenu(subMenu, "menu.tools.select");
		initMenuItem(subMenu, "menu.tools.scanStatistics", "Ctrl+T", new Integer(SWT.MOD1 | 'T'), initListener(ToolsActions.ScanStatistics.class));

		initMenuItem(selectMenu, "menu.tools.select.alive", null, null, initListener(ToolsActions.SelectAlive.class), true);
		initMenuItem(selectMenu, "menu.tools.select.dead", null, null, initListener(ToolsActions.SelectDead.class), true);
		initMenuItem(selectMenu, "menu.tools.select.withPorts", null, null, initListener(ToolsActions.SelectWithPorts.class), true);
		initMenuItem(selectMenu, "menu.tools.select.withoutPorts", null, null, initListener(ToolsActions.SelectWithoutPorts.class), true);
		initMenuItem(selectMenu, null, null, null, null);
		initMenuItem(selectMenu, "menu.tools.select.invert", "Ctrl+I", new Integer(SWT.MOD1 | 'I'), initListener(ToolsActions.SelectInvert.class), true);
		
		subMenu = initMenu(menu, "menu.help");
		initMenuItem(subMenu, "menu.help.gettingStarted", !Platform.MAC_OS ? "F1" : null, new Integer(Platform.MAC_OS ? SWT.HELP : SWT.F1), initListener(HelpMenuActions.GettingStarted.class));
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.website", null, null, initListener(HelpMenuActions.Website.class));
		initMenuItem(subMenu, "menu.help.faq", null, null, initListener(HelpMenuActions.FAQ.class));
		initMenuItem(subMenu, "menu.help.plugins", null, null, initListener(HelpMenuActions.Plugins.class));
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.cmdLine", null, null, initListener(HelpMenuActions.CommandLineUsage.class));
		
		if (!Platform.MAC_OS) {
			// mac will have these in the 'application' menu
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.checkVersion", null, null, initListener(HelpMenuActions.CheckVersion.class));
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.about", null, null, initListener(HelpMenuActions.About.class));
		}
	}

	private void createCommandsMenuItems(Menu menu) {
		initMenuItem(menu, "menu.commands.details", null, null, initListener(CommandsMenuActions.Details.class));
		initMenuItem(menu, null, null, null, null);
		initMenuItem(menu, "menu.commands.rescan", "Ctrl+R", new Integer(SWT.MOD1 | 'R'), initListener(CommandsMenuActions.Rescan.class), true);
		initMenuItem(menu, "menu.commands.delete", Platform.MAC_OS ? "⌦" : "Del", /* this is not a global key binding */ null, initListener(CommandsMenuActions.Delete.class), true);
		initMenuItem(menu, null, null, null, null);
		initMenuItem(menu, "menu.commands.copy", Platform.MAC_OS ? "⌘C" : "Ctrl+C", /* this is not a global key binding */ null, initListener(CommandsMenuActions.CopyIP.class));
		initMenuItem(menu, "menu.commands.copyDetails", null, null, initListener(CommandsMenuActions.CopyIPDetails.class));
		initMenuItem(menu, null, null, null, null);		
		createOpenersMenu(menu);
		// initMenuItem(subMenu, "menu.commands.show", null, initListener());
	}

	private void createOpenersMenu(Menu parentMenu) {
		OpenersMenu openersMenu = (OpenersMenu) container.getComponentInstance(OpenersMenu.class);
		MenuItem openersMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
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
	
	private Listener initListener(Class<? extends Listener> listenerClass) {
		// register the component if it is not registered yet
		if (container.getComponentAdapter(listenerClass) == null)
			container.registerComponentImplementation(listenerClass);
		// .. and create the instance, satisfying all the dependencies
		return (Listener) container.getComponentInstance(listenerClass);
	}
	
	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener) {
		return initMenuItem(parent, label, acceleratorText, accelerator, listener, false);
	}
	
	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener, boolean disableDuringScanning) {
		MenuItem menuItem = new MenuItem(parent, label == null ? SWT.SEPARATOR : SWT.PUSH);
		
		if (label != null) 
			menuItem.setText(Labels.getLabel(label) + (acceleratorText != null ? "\t" + acceleratorText : ""));
		
		if (accelerator != null)
			menuItem.setAccelerator(accelerator.intValue());
		
		if (listener != null)
			menuItem.addListener(SWT.Selection, listener);
		else
			menuItem.setEnabled(false);
		
		if (disableDuringScanning) {
			menuItem.setData("disableDuringScanning", Boolean.TRUE);
		}
		
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
		public OpenersMenu(Decorations parent, CommandsMenuActions.EditOpeners editOpenersListener, CommandsMenuActions.ShowOpenersMenu showOpenersMenuListener) {
			super(parent, SWT.DROP_DOWN);

			initMenuItem(this, "menu.commands.open.edit", null, null, editOpenersListener);
			initMenuItem(this, null, null, null, null);
			
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
		public FavoritesMenu(Decorations parent, FavoritesMenuActions.Add addListener, FavoritesMenuActions.Edit editListener, FavoritesMenuActions.ShowMenu showFavoritesMenuListener) {
			super(parent, SWT.DROP_DOWN);

			initMenuItem(this, "menu.favorites.add", "Ctrl+D", new Integer(SWT.MOD1 | 'D'), addListener);
			initMenuItem(this, "menu.favorites.edit", null, null, editListener);
			initMenuItem(this, null, null, null, null);
			
			addListener(SWT.Show, showFavoritesMenuListener);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * ColumnsMenu wrapper for type-safety.
	 * This is the menu when clicking on a column header.
	 */
	public static class ColumnsMenu extends Menu {
		public ColumnsMenu(Decorations parent, ColumnsActions.SortBy sortByListener, ColumnsActions.AboutFetcher aboutListener, ColumnsActions.FetcherPreferences preferencesListener) {
			super(parent, SWT.POP_UP);
			
			initMenuItem(this, "menu.columns.sortBy", null, null, sortByListener);
			initMenuItem(this, "menu.columns.preferences", null, null, preferencesListener);
			initMenuItem(this, "menu.columns.about", null, null, aboutListener);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}

	/**
	 * State transition listener in order to enable/disable menu items of the 
	 * specified menu.
	 */
	public static class MenuEnablerDisabler implements StateTransitionListener {
		private Menu menu;
		
		public MenuEnablerDisabler(Menu menu) {
			this.menu = menu;
		}

		public void transitionTo(final ScanningState state, Transition transition) {
			if (transition != Transition.START && transition != Transition.COMPLETE)
				return;
			processMenu(menu, state == ScanningState.IDLE);
		}

		public void processMenu(Menu menu, boolean isEnabled) {
			// processes menu items recursively
			for (MenuItem item : menu.getItems()) {
				if (item.getData("disableDuringScanning") == Boolean.TRUE) {
					item.setEnabled(isEnabled);
				}
				else 
				if (item.getMenu() != null) {
					processMenu(item.getMenu(), isEnabled);
				}
			}
		}
	}
}
