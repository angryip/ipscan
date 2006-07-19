/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.actions.ColumnsActions;
import net.azib.ipscan.gui.actions.CommandsActions;
import net.azib.ipscan.gui.actions.FavoritesActions;
import net.azib.ipscan.gui.actions.FileActions;
import net.azib.ipscan.gui.actions.HelpActions;
import net.azib.ipscan.gui.actions.ToolsActions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * MainMenu
 *
 * @author anton
 */
public class MainMenu {
	
	private MainWindow mainWindow;
	private Menu mainMenu;
	private Menu resultsContextMenu;
	private Menu favoritesMenu;
	private Menu columnsMenu;
		
	public MainMenu(MainWindow mainWindow) {
		
		this.mainWindow = mainWindow;
		Shell shell = mainWindow.getShell();
		
		mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);		
				
		Object[] menuDefinition = createMenuDefinition();
		
		// generate the menu from the definition
		generateMenu(shell, menuDefinition, mainMenu);
		
		// retrieve results context menu, that is the same as "commands" menu
		// note: the index of 2 is hardcoded and may theoretically change
		// TODO: probably something better should be done here
		resultsContextMenu = new Menu(shell, SWT.POP_UP);
		generateSubMenu((Object[]) ((Object[]) menuDefinition[2])[1], resultsContextMenu);		
		
		// retrieve favoritesMenu, which is 3
		favoritesMenu = mainMenu.getItem(3).getMenu();
		favoritesMenu.addListener(SWT.Show, new FavoritesActions.ShowMenu(mainWindow, favoritesMenu));
		
		// this is the menu when clicking on a column header
		columnsMenu = new Menu(shell, SWT.POP_UP);
		generateSubMenu(createColumnsMenuDefinition(), columnsMenu);
	}
	
	private Object[] createMenuDefinition() {
		// a shortened version of menu definition
		Object[] menuDefinition = new Object[] {
			new Object[] {"menu.file",  
				new Object[] {
					new Object[] {"menu.file.saveAll", new Integer(SWT.CONTROL | 'S'), new FileActions.SaveResults(mainWindow, false)},
					new Object[] {"menu.file.saveSelection", null, new FileActions.SaveResults(mainWindow, true)},
					null,
					new Object[] {"menu.file.exportOptions", null, null},
					new Object[] {"menu.file.importOptions", null, null},
					null,
					new Object[] {"menu.file.exit", null, new FileActions.Exit()},
				}	
			},
			new Object[] {"menu.goto",  
				new Object[] {
					new Object[] {"menu.goto.aliveHost", new Integer(SWT.CONTROL | 'H'), null},
					new Object[] {"menu.goto.deadHost", new Integer(SWT.CONTROL | 'E'), null},
					new Object[] {"menu.goto.openPort", new Integer(SWT.CONTROL | 'P'), null},
					new Object[] {"menu.goto.closedPort", new Integer(SWT.CONTROL | 'L'), null},
					null,
					new Object[] {"menu.goto.find", new Integer(SWT.CONTROL | 'F'), null},
				}	
			},
			new Object[] {"menu.commands",  
				new Object[] {
					new Object[] {"menu.commands.details", null, new CommandsActions.Details(mainWindow.getResultTable())},
					null,
					new Object[] {"menu.commands.rescan", new Integer(SWT.CONTROL | 'R'), null},
					new Object[] {"menu.commands.delete", new Integer(SWT.DEL), null},
					null,
					new Object[] {"menu.commands.copy", new Integer(SWT.CONTROL | 'C'), null},
					new Object[] {"menu.commands.copyDetails", null, null},
					null,
					new Object[] {"menu.commands.show", null, null},
					new Object[] {"menu.commands.open", null, null},
				}	
			},
			new Object[] {"menu.favorites",  
				new Object[] {
					new Object[] {"menu.favorites.add", new Integer(SWT.CONTROL | 'D'), new FavoritesActions.Add(mainWindow)},
					new Object[] {"menu.favorites.edit", null, new FavoritesActions.Edit()},
					null,
				}	
			},
			new Object[] {"menu.tools",  
				new Object[] {
					new Object[] {"menu.tools.options", new Integer(SWT.CONTROL | 'O'), new ToolsActions.Options()},
					new Object[] {"menu.tools.fetchers", null, null},
					null,
					new Object[] {"menu.tools.delete", null, null},
					new Object[] {"menu.tools.lastInfo", new Integer(SWT.CONTROL | 'I'), null},
				}	
			},
			new Object[] {"menu.help",  
				new Object[] {
					new Object[] {"menu.help.gettingStarted", new Integer(SWT.F1), new HelpActions.GettingStarted()},
					null,
					new Object[] {"menu.help.website", null, new HelpActions.Website()},
					new Object[] {"menu.help.forum", null, null},
					new Object[] {"menu.help.plugins", null, null},
					null,
					new Object[] {"menu.help.cmdLine", null, null},
					null,
					new Object[] {"menu.help.checkVersion", null, null},
					null,
					new Object[] {"menu.help.about", new Integer(SWT.F12), new HelpActions.About()},
				}	
			},
		};
		return menuDefinition;
	}

	private Object[] createColumnsMenuDefinition() {
		// a shortened version of menu definition
		Object[] menuDefinition = new Object[] {
			new Object[] {"menu.columns.sortBy", null, new ColumnsActions.SortBy()},
			new Object[] {"menu.columns.info", null, null},
			new Object[] {"menu.columns.options", null, null},
		};
		
		return menuDefinition;
	}

	/**
	 * Generates a menu according to the menu definition
	 * @param shell
	 * @param menuDefinition
	 * @param menu the menu, where to append the generated menu
	 */
	private void generateMenu(final Shell shell, Object[] menuDefinition, Menu menu) {
		Labels labels = Labels.getInstance();
		
		for (int i = 0; i < menuDefinition.length; i++) {
			Object[] topMenuDef = (Object[]) menuDefinition[i];
			
			MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
			menuItem.setText(labels.getString((String) topMenuDef[0]));
			
			Object[] subMenuDef = (Object[]) topMenuDef[1];
			Menu subMenu = new Menu(shell, SWT.DROP_DOWN);
			menuItem.setMenu(subMenu);
			
			generateSubMenu(subMenuDef, subMenu);
		}
	}

	/**
	 * Generates a submenu according to the definition
	 * @param menuDefinition
	 * @param menu
	 */
	private void generateSubMenu(Object[] menuDefinition, Menu menu) {		
		Labels labels = Labels.getInstance();
		
		for (int j = 0; j < menuDefinition.length; j++) {
			Object[] menuDef = (Object[]) menuDefinition[j];
			
			if (menuDef == null) {
				new MenuItem(menu, SWT.SEPARATOR);
			}
			else {
				MenuItem subItem = new MenuItem(menu, SWT.PUSH);
				subItem.setText(labels.getString((String) menuDef[0]));
				if (menuDef[1] != null)
					subItem.setAccelerator(((Integer)menuDef[1]).intValue());
				if (menuDef[2] != null)
					subItem.addListener(SWT.Selection, (Listener) menuDef[2]);
				else
					subItem.setEnabled(false);
			}
		}
	}
	
	public Menu getResultsContextMenu() {
		return resultsContextMenu;
	}

	public Menu getFavoritesMenu() {
		return favoritesMenu;
	}
	
	public Menu getColumnsPopupMenu() {
		return columnsMenu;
	}
	
}
