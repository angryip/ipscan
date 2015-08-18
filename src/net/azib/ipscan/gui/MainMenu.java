/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.menu.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * MainMenu
 *
 * @author Anton Keks
 */
public class MainMenu {

	@Inject ScanMenu scanMenu;
	@Inject GotoMenu gotoMenu;
	@Inject CommandsMenu commandsMenu;
	@Inject FavoritesMenu favoritesMenu;
	@Inject ToolsMenu toolsMenu;
	@Inject HelpMenu helpMenu;

	private final Menu mainMenu;

	@Inject public MainMenu(Shell parent, @Named("mainMenu") Menu mainMenu, ResultsContextMenu resultsContextMenu, StateMachine stateMachine) {

		this.mainMenu = mainMenu;

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

		//// commands
		{
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(Labels.getLabel("menu.commands"));

			menuItem.setMenu(commandsMenu);
		}

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
