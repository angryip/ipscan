package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.DefaultOpenerConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class CommandsMenu extends AbstractMenu {
	public CommandsMenu(Shell parent, CommandsMenuActions actions, OpenersMenu openersMenu,
						OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig, ResultTable resultTable) {
		super(parent, SWT.DROP_DOWN);
		build(actions, openersMenu, openersConfig, defaultOpenerConfig, resultTable);
	}

	protected CommandsMenu(Shell parent, int style) {
		super(parent, style);
	}

	protected void build(CommandsMenuActions actions, OpenersMenu openersMenu,
						 OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig, ResultTable resultTable) {
		initMenuItem(this, "menu.commands.details", null, null, actions.details);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.rescan", "Ctrl+R", SWT.MOD1 | 'R', actions.rescan, true);
		initMenuItem(this, "menu.commands.delete", Platform.MAC_OS ? "⌦" : "Del", /* this is not a global key binding */ null, actions.delete, true);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.copy", Platform.MAC_OS ? "⌘C" : "Ctrl+C", /* this is not a global key binding */ null, actions.copyIP);
		initMenuItem(this, "menu.commands.copyDetails", null, null, actions.copyIPDetails);
		initMenuItem(this, null, null, null, null);

		var openersMenuItem = new MenuItem(this, SWT.CASCADE);
		openersMenuItem.setText(Labels.getLabel(openersMenu.getId()));
		openersMenuItem.setMenu(openersMenu);

		// "Set Opener" submenu: pick the default Opener for the selected IP(s)
		var setOpenerItem = new MenuItem(this, SWT.CASCADE);
		setOpenerItem.setText(Labels.getLabel("menu.commands.setOpener"));
		var setOpenerMenu = new Menu(setOpenerItem);
		setOpenerItem.setMenu(setOpenerMenu);
		setOpenerMenu.addListener(SWT.Show, e -> {
			for (var item : setOpenerMenu.getItems()) item.dispose();
			var selection = resultTable.getSelectionIndices();
			var defaultName = selection.length > 0
				? defaultOpenerConfig.get(resultTable.getScanningResults().getResult(selection[0]).getAddress().getHostAddress())
				: null;
			for (var name : openersConfig) {
				var item = new MenuItem(setOpenerMenu, SWT.CHECK);
				item.setText(name);
				item.setData(name);
				item.setSelection(name.equals(defaultName));
				item.addListener(SWT.Selection, actions.setDefaultOpener);
			}
		});

		// "Open by" the configured default Opener for the selected IP(s)
		var openByItem = new MenuItem(this, SWT.PUSH);
		openByItem.addListener(SWT.Selection, actions.openBy);

		// refresh the "Open by" item each time the menu is shown
		this.addListener(SWT.Show, e -> {
			var selection = resultTable.getSelectionIndices();
			var defaultName = selection.length > 0
				? defaultOpenerConfig.get(resultTable.getScanningResults().getResult(selection[0]).getAddress().getHostAddress())
				: null;
			if (defaultName != null) {
				openByItem.setText(Labels.getLabel("menu.commands.openBy") + " " + defaultName);
				openByItem.setEnabled(true);
			}
			else {
				openByItem.setText(Labels.getLabel("menu.commands.openBy"));
				openByItem.setEnabled(false);
			}
		});
	}

	@Override
	public String getId() {
		return "menu.commands";
	}
}
