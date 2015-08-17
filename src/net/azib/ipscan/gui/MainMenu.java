/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.actions.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * MainMenu
 *
 * @author Anton Keks
 */
public class MainMenu {

	@Inject ScanMenuActions.LoadFromFile loadFromFile;
	@Inject ScanMenuActions.SaveAll saveAll;
	@Inject ScanMenuActions.SaveSelection saveSelection;
	@Inject ScanMenuActions.Quit quit;

	@Inject GotoMenuActions.NextAliveHost nextAliveHost;
	@Inject GotoMenuActions.NextHostWithInfo nextHostWithInfo;
	@Inject GotoMenuActions.NextDeadHost nextDeadHost;
	@Inject GotoMenuActions.PrevAliveHost prevAliveHost;
	@Inject GotoMenuActions.PrevHostWithInfo prevHostWithInfo;
	@Inject GotoMenuActions.PrevDeadHost prevDeadHost;
	@Inject GotoMenuActions.Find find;

	@Inject ToolsActions.Preferences preferences;
	@Inject ToolsActions.ChooseFetchers chooseFetchers;
	@Inject ToolsActions.ScanStatistics scanStatistics;
	@Inject ToolsActions.SelectAlive selectAlive;
	@Inject ToolsActions.SelectDead selectDead;
	@Inject ToolsActions.SelectWithPorts selectWithPorts;
	@Inject ToolsActions.SelectWithoutPorts selectWithoutPorts;
	@Inject ToolsActions.SelectInvert selectInvert;

	@Inject HelpMenuActions.GettingStarted gettingStarted;
	@Inject HelpMenuActions.Website website;
	@Inject HelpMenuActions.FAQ faq;
	@Inject HelpMenuActions.Plugins plugins;
	@Inject HelpMenuActions.CommandLineUsage commandLineUsage;
	@Inject HelpMenuActions.CheckVersion checkVersion;
	@Inject HelpMenuActions.About about;

	@Inject CommandsMenuActions.Details details;
	@Inject CommandsMenuActions.Rescan rescan;
	@Inject CommandsMenuActions.Delete delete;
	@Inject CommandsMenuActions.CopyIP copyIP;
	@Inject CommandsMenuActions.CopyIPDetails copyIPDetails;

	@Inject Provider<OpenersMenu> openersMenuProvider;
	@Inject Provider<FavoritesMenu> favoritesMenuProvider;

	private final Menu mainMenu, resultsContextMenu;

	@Inject public MainMenu(Shell shell, @Named("mainMenu") Menu mainMenu, @Named("commandsMenu") Menu resultsContextMenu, StateMachine stateMachine) {

		this.mainMenu = mainMenu;
		this.resultsContextMenu = resultsContextMenu;

		shell.setMenuBar(mainMenu);		

		stateMachine.addTransitionListener(new MenuEnablerDisabler(mainMenu));
		stateMachine.addTransitionListener(new MenuEnablerDisabler(resultsContextMenu));
	}

	void prepare() {
		createMainMenuItems(mainMenu);
		createCommandsMenuItems(resultsContextMenu);
	}

	private void createMainMenuItems(Menu menu) {
		
		Menu subMenu = initMenu(menu, "menu.scan");
//		initMenuItem(subMenu, "menu.scan.newWindow", "Ctrl+N", new Integer(SWT.MOD1 | 'N'), initListener(FileActions.NewWindow.class));
//		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.scan.load", "", SWT.MOD1 | 'O', loadFromFile, true);
		initMenuItem(subMenu, "menu.scan.exportAll", "Ctrl+S", SWT.MOD1 | 'S', saveAll, false);
		initMenuItem(subMenu, "menu.scan.exportSelection", null, null, saveSelection, false);
//		initMenuItem(subMenu, null, null, null, null);
//		initMenuItem(subMenu, "menu.scan.exportPreferences", null, null, null);
//		initMenuItem(subMenu, "menu.scan.importPreferences", null, null, null);
		if (!Platform.MAC_OS) {
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.scan.quit", "Ctrl+Q", SWT.MOD1 | 'Q', quit);
		}
				subMenu = initMenu(menu, "menu.goto");
		initMenuItem(subMenu, "menu.goto.next.aliveHost", "Ctrl+H", SWT.MOD1 | 'H', nextAliveHost);
		initMenuItem(subMenu, "menu.goto.next.openPort", "Ctrl+J", SWT.MOD1 | 'J', nextHostWithInfo);
		initMenuItem(subMenu, "menu.goto.next.deadHost", "Ctrl+K", SWT.MOD1 | 'K', nextDeadHost);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.goto.prev.aliveHost", "Ctrl+Shift+H", SWT.MOD1 | SWT.MOD2 | 'H', prevAliveHost);
		initMenuItem(subMenu, "menu.goto.prev.openPort", "Ctrl+Shift+J", SWT.MOD1 | SWT.MOD2 | 'J', prevHostWithInfo);
		initMenuItem(subMenu, "menu.goto.prev.deadHost", "Ctrl+Shift+K", SWT.MOD1 | SWT.MOD2 | 'K', prevDeadHost);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.goto.find", "Ctrl+F", SWT.MOD1 | 'F', find);
		
		subMenu = initMenu(menu, "menu.commands");
		createCommandsMenuItems(subMenu);

		createFavoritesMenu(menu);
		
		subMenu = initMenu(menu, "menu.tools");
		initMenuItem(subMenu, "menu.tools.preferences", "Ctrl+Shift+P", SWT.MOD1 | (Platform.MAC_OS ? ',' : SWT.MOD2 | 'P'), preferences, true);
		initMenuItem(subMenu, "menu.tools.fetchers", "Ctrl+Shift+O", SWT.MOD1 | SWT.MOD2 | (Platform.MAC_OS ? ',' : 'O'), chooseFetchers, true);
		initMenuItem(subMenu, null, null, null, null);
		Menu selectMenu = initMenu(subMenu, "menu.tools.select");
		initMenuItem(subMenu, "menu.tools.scanStatistics", "Ctrl+T", SWT.MOD1 | 'T', scanStatistics);

		initMenuItem(selectMenu, "menu.tools.select.alive", null, null, selectAlive, true);
		initMenuItem(selectMenu, "menu.tools.select.dead", null, null, selectDead, true);
		initMenuItem(selectMenu, "menu.tools.select.withPorts", null, null, selectWithPorts, true);
		initMenuItem(selectMenu, "menu.tools.select.withoutPorts", null, null, selectWithoutPorts, true);
		initMenuItem(selectMenu, null, null, null, null);
		initMenuItem(selectMenu, "menu.tools.select.invert", "Ctrl+I", SWT.MOD1 | 'I', selectInvert, true);
		
		subMenu = initMenu(menu, "menu.help");
		initMenuItem(subMenu, "menu.help.gettingStarted", !Platform.MAC_OS ? "F1" : null, Platform.MAC_OS ? SWT.HELP : SWT.F1, gettingStarted);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.website", null, null, website);
		initMenuItem(subMenu, "menu.help.faq", null, null, faq);
		initMenuItem(subMenu, "menu.help.plugins", null, null, plugins);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.cmdLine", null, null, commandLineUsage);
		
		if (!Platform.MAC_OS) {
			// mac will have these in the 'application' menu
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.checkVersion", null, null, checkVersion);
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.about", null, null, about);
		}
	}

	private void createCommandsMenuItems(Menu menu) {
		initMenuItem(menu, "menu.commands.details", null, null, details);
		initMenuItem(menu, null, null, null, null);
		initMenuItem(menu, "menu.commands.rescan", "Ctrl+R", SWT.MOD1 | 'R', rescan, true);
		initMenuItem(menu, "menu.commands.delete", Platform.MAC_OS ? "⌦" : "Del", /* this is not a global key binding */ null, delete, true);
		initMenuItem(menu, null, null, null, null);
		initMenuItem(menu, "menu.commands.copy", Platform.MAC_OS ? "⌘C" : "Ctrl+C", /* this is not a global key binding */ null, copyIP);
		initMenuItem(menu, "menu.commands.copyDetails", null, null, copyIPDetails);
		initMenuItem(menu, null, null, null, null);		
		createOpenersMenu(menu);
		// initMenuItem(subMenu, "menu.commands.show", null, initListener());
	}

	private void createOpenersMenu(Menu parentMenu) {
		OpenersMenu openersMenu = openersMenuProvider.get();
		MenuItem openersMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
		openersMenuItem.setText(Labels.getLabel("menu.commands.open"));
		openersMenuItem.setMenu(openersMenu);
	}

	private void createFavoritesMenu(Menu parentMenu) {
		MenuItem favoritesMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
		favoritesMenuItem.setText(Labels.getLabel("menu.favorites"));
		Menu favoritesMenu = favoritesMenuProvider.get();
		favoritesMenuItem.setMenu(favoritesMenu);
	}

	private static Menu initMenu(Menu menu, String label) {
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Labels.getLabel(label));
		
		Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
		menuItem.setMenu(subMenu);
		
		return subMenu;
	}

	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener) {
		return initMenuItem(parent, label, acceleratorText, accelerator, listener, false);
	}
	
	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener, boolean disableDuringScanning) {
		MenuItem menuItem = new MenuItem(parent, label == null ? SWT.SEPARATOR : SWT.PUSH);
		
		if (label != null) 
			menuItem.setText(Labels.getLabel(label) + (acceleratorText != null ? "\t" + acceleratorText : ""));
		
		if (accelerator != null)
			menuItem.setAccelerator(accelerator);
		
		if (listener != null)
			menuItem.addListener(SWT.Selection, listener);
		else
			menuItem.setEnabled(false);
		
		if (disableDuringScanning) {
			menuItem.setData("disableDuringScanning", true);
		}
		
		return menuItem;
	}

	/**
	 * CommandsMenu wrapper for type-safety
	 */
	public static class CommandsMenu extends Menu {
		public CommandsMenu(Shell parent) {
			super(parent, SWT.POP_UP);
		}
		
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * OpenersMenu wrapper for type-safety
	 */
	public static class OpenersMenu extends Menu {
		@Inject
		public OpenersMenu(Shell parent, CommandsMenuActions.EditOpeners editOpenersListener, CommandsMenuActions.ShowOpenersMenu showOpenersMenuListener) {
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
		@Inject
		public FavoritesMenu(Shell parent, FavoritesMenuActions.Add addListener, FavoritesMenuActions.Edit editListener, FavoritesMenuActions.ShowMenu showFavoritesMenuListener) {
			super(parent, SWT.DROP_DOWN);

			initMenuItem(this, "menu.favorites.add", "Ctrl+D", SWT.MOD1 | 'D', addListener);
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
		@Inject public ColumnsMenu(Shell parent, ColumnsActions.SortBy sortByListener, ColumnsActions.AboutFetcher aboutListener, ColumnsActions.FetcherPreferences preferencesListener) {
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
