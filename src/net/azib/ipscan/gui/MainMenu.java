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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * MainMenu
 *
 * @author anton
 */
public class MainMenu {
	
	private MutablePicoContainer container;
	
	private Menu mainMenu;
	private ResultsContextMenu resultsContextMenu;
	private FavoritesMenu favoritesMenu;
	private ColumnsMenu columnsMenu;
	private OpenersMenu openersMenu;
		
	public MainMenu(Shell shell, PicoContainer parentContainer) {
		
		// create the menu-specific child container
		this.container = new DefaultPicoContainer(parentContainer);
		
		// register some components not registered in the main menu
		container.registerComponentImplementation(CommandsActions.SelectOpener.class);
		container.registerComponentImplementation(CommandsActions.ShowOpenersMenu.class);
		container.registerComponentImplementation(FavoritesActions.ShowMenu.class);
		container.registerComponentImplementation(FavoritesActions.Select.class);		

		mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);		
				
		Object[] menuDefinition = createMenuDefinition();
		
		// generate the menu from the definition
		generateMenu(shell, menuDefinition, mainMenu);
		
		openersMenu = new OpenersMenu(shell);
		container.registerComponentInstance(OpenersMenu.class, openersMenu);
		
		MenuItem openersMenuItem = new MenuItem(mainMenu.getItem(2).getMenu(), SWT.CASCADE);
		openersMenuItem.setText(Labels.getInstance().getString("menu.commands.open"));
		openersMenuItem.setMenu(openersMenu);
		Listener showOpenersMenuListener = (Listener) container.getComponentInstance(CommandsActions.ShowOpenersMenu.class);
		openersMenu.addListener(SWT.Show, showOpenersMenuListener);
		MenuItem menuItem = new MenuItem(openersMenu, SWT.PUSH);
		menuItem.setText(Labels.getInstance().getString("menu.commands.open.edit"));
		menuItem.addListener(SWT.Selection, new CommandsActions.EditOpeners());
		menuItem = new MenuItem(openersMenu, SWT.SEPARATOR);
		// run the listener to populate the menu initially and initialize accelerators
		showOpenersMenuListener.handleEvent(null);
		
		// retrieve results context menu, that is the same as "commands" menu
		// note: the index of 2 is hardcoded and may theoretically change
		// TODO: probably something better should be done here
		resultsContextMenu = new ResultsContextMenu(shell);
		generateSubMenu((Object[]) ((Object[]) menuDefinition[2])[1], resultsContextMenu);		
		
		// retrieve favoritesMenu, which is 3 (TODO: ugly hardcode of favorites menu retrieval)
		favoritesMenu = (FavoritesMenu) mainMenu.getItem(3).getMenu();
		container.registerComponentInstance("favoritesMenu", favoritesMenu);
		favoritesMenu.addListener(SWT.Show, (Listener) container.getComponentInstance(FavoritesActions.ShowMenu.class));
	}

	// TODO: convert this mess to normal code: make a custom MenuItem, which accepts stuff into the constructor
	private static Object[] createMenuDefinition() {
		// a shortened version of menu definition
		Object[] menuDefinition = new Object[] {
			new Object[] {"menu.file",  
				new Object[] {
					new Object[] {"menu.file.saveAll", new Integer(SWT.CONTROL | 'S'), FileActions.SaveAll.class},
					new Object[] {"menu.file.saveSelection", null, FileActions.SaveSelection.class},
					null,
					new Object[] {"menu.file.exportOptions", null, null},
					new Object[] {"menu.file.importOptions", null, null},
					null,
					new Object[] {"menu.file.exit", null, FileActions.Exit.class},
				}	
			},
			new Object[] {"menu.goto",  
				new Object[] {
					new Object[] {"menu.goto.aliveHost", new Integer(SWT.CONTROL | SWT.SHIFT | 'H'), GotoActions.NextAliveHost.class},
					new Object[] {"menu.goto.deadHost", new Integer(SWT.CONTROL | SWT.SHIFT | 'D'), GotoActions.NextDeadHost.class},
					new Object[] {"menu.goto.openPort", new Integer(SWT.CONTROL | SWT.SHIFT | 'P'), GotoActions.NextHostWithInfo.class},
					null,
					new Object[] {"menu.goto.find", new Integer(SWT.CONTROL | 'F'), GotoActions.Find.class},
				}	
			},
			new Object[] {"menu.commands",  
				new Object[] {
					new Object[] {"menu.commands.details", null, CommandsActions.Details.class},
					null,
					new Object[] {"menu.commands.rescan", new Integer(SWT.CONTROL | 'R'), null},
					new Object[] {"menu.commands.delete", new Integer(SWT.DEL), CommandsActions.Delete.class},
					null,
					new Object[] {"menu.commands.copy", new Integer(SWT.CONTROL | 'C'), CommandsActions.CopyIP.class},
					new Object[] {"menu.commands.copyDetails", null, CommandsActions.CopyIPDetails.class},
					null,
					//new Object[] {"menu.commands.show", null, null},
				}	
			},
			new Object[] {"menu.favorites",  
				new Object[] {
					new Object[] {"menu.favorites.add", new Integer(SWT.CONTROL | 'D'), FavoritesActions.Add.class},
					new Object[] {"menu.favorites.edit", null, FavoritesActions.Edit.class},
					null,
				}	
			},
			new Object[] {"menu.tools",  
				new Object[] {
					new Object[] {"menu.tools.options", new Integer(SWT.CONTROL | 'O'), ToolsActions.Options.class},
					new Object[] {"menu.tools.fetchers", null, null},
					null,
					new Object[] {"menu.tools.delete", null, null},
					new Object[] {"menu.tools.lastInfo", new Integer(SWT.CONTROL | 'I'), null},
				}	
			},
			new Object[] {"menu.help",  
				new Object[] {
					new Object[] {"menu.help.gettingStarted", new Integer(SWT.F1), HelpActions.GettingStarted.class},
					null,
					new Object[] {"menu.help.website", null, HelpActions.Website.class},
					new Object[] {"menu.help.forum", null, HelpActions.Forum.class},
					new Object[] {"menu.help.plugins", null, HelpActions.Plugins.class},
					null,
					new Object[] {"menu.help.cmdLine", null, null},
					null,
					new Object[] {"menu.help.checkVersion", null, HelpActions.CheckVersion.class},
					null,
					new Object[] {"menu.help.about", new Integer(SWT.F12), HelpActions.About.class},
				}	
			},
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
			// TODO: ugly hardcode of FavoritesMenu creation
			Menu subMenu = i == 3 ? new FavoritesMenu(shell) : new Menu(shell, SWT.DROP_DOWN);
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
				
				if (menuDef[2] != null) {
					// register the component if it is not registered yet
					if (container.getComponentAdapter(menuDef[2]) == null)
						container.registerComponentImplementation((Class) menuDef[2]);
					// .. and create the instance, satisfying all the dependencies
					subItem.addListener(SWT.Selection, (Listener) container.getComponentInstance(menuDef[2]));
				}
				else {
					subItem.setEnabled(false);
				}
			}
		}
	}
	
	public Menu getResultsContextMenu() {
		return resultsContextMenu;
	}

	public Menu getFavoritesMenu() {
		return favoritesMenu;
	}
	
	public ColumnsMenu getColumnsPopupMenu() {
		return columnsMenu;
	}

	/**
	 * OpenersMenu wrapper for type-safety
	 */
	public static class OpenersMenu extends Menu {
		public OpenersMenu(Decorations parent) {
			super(parent, SWT.DROP_DOWN);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * ResultsContextMenu wrapper for type-safety
	 */
	public static class ResultsContextMenu extends Menu {
		public ResultsContextMenu(Decorations parent) {
			super(parent, SWT.POP_UP);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
	/**
	 * FavoritesMenu wrapper for type-safety
	 */
	public static class FavoritesMenu extends Menu {
		public FavoritesMenu(Decorations parent) {
			super(parent, SWT.DROP_DOWN);
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
			
			MenuItem item = new MenuItem(this, SWT.PUSH);
			item.setText(Labels.getInstance().getString("menu.columns.sortBy"));
			item.addListener(SWT.Selection, sortByListener);
			
			item = new MenuItem(this, SWT.PUSH);
			item.setText(Labels.getInstance().getString("menu.columns.info"));
			item.setEnabled(false);
			
			item = new MenuItem(this, SWT.PUSH);
			item.setText(Labels.getInstance().getString("menu.columns.options"));
			item.setEnabled(false);
		}
		protected void checkSubclass() { } // allow extending of Menu class
	}
	
}
