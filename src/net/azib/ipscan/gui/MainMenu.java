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
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import net.azib.ipscan.gui.menu.*;
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

	@Inject ScanMenu scanMenu;
	@Inject GotoMenu gotoMenu;

	@Inject CommandsMenuActions.Details details;
	@Inject CommandsMenuActions.Rescan rescan;
	@Inject CommandsMenuActions.Delete delete;
	@Inject CommandsMenuActions.CopyIP copyIP;
	@Inject CommandsMenuActions.CopyIPDetails copyIPDetails;

	@Inject FavoritesMenu favoritesMenu;
	@Inject ToolsMenu toolsMenu;
	@Inject HelpMenu helpMenu;

	@Inject Provider<OpenersMenu> openersMenuProvider;

	private final Menu mainMenu, resultsContextMenu;

	@Inject public MainMenu(Shell parent, @Named("mainMenu") Menu mainMenu, ResultsContextMenu resultsContextMenu, StateMachine stateMachine) {

		this.mainMenu = mainMenu;
		this.resultsContextMenu = resultsContextMenu;

		parent.setMenuBar(mainMenu);

		stateMachine.addTransitionListener(new MenuEnablerDisabler(mainMenu));
		stateMachine.addTransitionListener(new MenuEnablerDisabler(resultsContextMenu));
	}

	void prepare() {
		createMainMenuItems(mainMenu);
	}

	private void createMainMenuItems(Menu menu) {
		
		//// scan
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.scan"));

			menuItem.setMenu(scanMenu);
		}

		//// goto
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.goto"));

			menuItem.setMenu(gotoMenu);
		}

		Menu subMenu = initMenu(menu, "menu.commands");
		createCommandsMenuItems(subMenu);

		//// favorites
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.favorites"));

			menuItem.setMenu(favoritesMenu);
		}

		//// tools
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.tools"));

			menuItem.setMenu(toolsMenu);
		}

		//// help
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.help"));

			menuItem.setMenu(helpMenu);
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
