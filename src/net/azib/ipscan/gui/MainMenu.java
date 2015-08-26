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
import javax.inject.Singleton;

/**
 * MainMenu
 *
 * @author Anton Keks
 */
@Singleton
public class MainMenu {

	@Inject
	public MainMenu(Shell parent, @Named("mainMenu") Menu mainMenu,
					ScanMenu scanMenu,
					GotoMenu gotoMenu,
					CommandsMenu commandsMenu,
					FavoritesMenu favoritesMenu,
					ToolsMenu toolsMenu,
					HelpMenu helpMenu,
					ResultsContextMenu resultsContextMenu,
					StateMachine stateMachine) {

		parent.setMenuBar(mainMenu);

		addMenuItem(mainMenu, scanMenu);
		addMenuItem(mainMenu, gotoMenu);
		addMenuItem(mainMenu, commandsMenu);
		addMenuItem(mainMenu, favoritesMenu);
		addMenuItem(mainMenu, toolsMenu);
		addMenuItem(mainMenu, helpMenu);

		stateMachine.addTransitionListener(new MenuEnablerDisabler(mainMenu));
		stateMachine.addTransitionListener(new MenuEnablerDisabler(resultsContextMenu));
	}

	private void addMenuItem(Menu mainMenu, AbstractMenu menu) {
		MenuItem menuItem = new MenuItem(mainMenu, SWT.CASCADE);
		menuItem.setText(Labels.getLabel(menu.getId()));
		menuItem.setMenu(menu);
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
