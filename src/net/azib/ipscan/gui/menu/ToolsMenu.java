package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.actions.ToolsActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ToolsMenu extends AbstractMenu {

	@Inject
		public ToolsMenu(Shell parent,
				ToolsActions.Preferences preferences,
				ToolsActions.ChooseFetchers chooseFetchers,
				ToolsActions.ScanStatistics scanStatistics,
				ToolsActions.SelectAlive selectAlive,
				ToolsActions.SelectDead selectDead,
				ToolsActions.SelectWithPorts selectWithPorts,
				ToolsActions.SelectWithoutPorts selectWithoutPorts,
				ToolsActions.SelectInvert selectInvert) {

		super(parent);

		initMenuItem(this, "menu.tools.preferences", "Ctrl+Shift+P", SWT.MOD1 | (Platform.MAC_OS ? ',' : SWT.MOD2 | 'P'), preferences, true);
		initMenuItem(this, "menu.tools.fetchers", "Ctrl+Shift+O", SWT.MOD1 | SWT.MOD2 | (Platform.MAC_OS ? ',' : 'O'), chooseFetchers, true);
		initMenuItem(this, null, null, null, null);
		Menu selectMenu = initMenu(this, "menu.tools.select");
		initMenuItem(this, "menu.tools.scanStatistics", "Ctrl+T", SWT.MOD1 | 'T', scanStatistics);

		initMenuItem(selectMenu, "menu.tools.select.alive", null, null, selectAlive, true);
		initMenuItem(selectMenu, "menu.tools.select.dead", null, null, selectDead, true);
		initMenuItem(selectMenu, "menu.tools.select.withPorts", null, null, selectWithPorts, true);
		initMenuItem(selectMenu, "menu.tools.select.withoutPorts", null, null, selectWithoutPorts, true);
		initMenuItem(selectMenu, null, null, null, null);
		initMenuItem(selectMenu, "menu.tools.select.invert", "Ctrl+I", SWT.MOD1 | 'I', selectInvert, true);
	}

	private static Menu initMenu(Menu menu, String label) {
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Labels.getLabel(label));

		Menu subMenu = new Menu(menu.getShell(), SWT.DROP_DOWN);
		menuItem.setMenu(subMenu);

		return subMenu;
	}

	@Override
	public String getId() {
		return "menu.tools";
	}
}
