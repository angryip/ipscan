package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CommandsMenu extends AbstractMenu {

	@Inject public CommandsMenu(Shell parent, CommandsMenuActions actions, OpenersMenu openersMenu) {
		this(parent, SWT.DROP_DOWN, actions, openersMenu);
	}

	protected CommandsMenu(Shell parent, int style, CommandsMenuActions actions, OpenersMenu openersMenu) {
		super(parent, style);

		initMenuItem(this, "menu.commands.details", null, null, actions.details);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.rescan", "Ctrl+R", SWT.MOD1 | 'R', actions.rescan, true);
		initMenuItem(this, "menu.commands.delete", Platform.MAC_OS ? "⌦" : "Del", /* this is not a global key binding */ null, actions.delete, true);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.copy", Platform.MAC_OS ? "⌘C" : "Ctrl+C", /* this is not a global key binding */ null, actions.copyIP);
		initMenuItem(this, "menu.commands.copyDetails", null, null, actions.copyIPDetails);
		initMenuItem(this, null, null, null, null);

		MenuItem openersMenuItem = new MenuItem(this, SWT.CASCADE);
		openersMenuItem.setText(Labels.getLabel(openersMenu.getId()));
		openersMenuItem.setMenu(openersMenu);

		// initMenuItem(subMenu, "menu.commands.show", null, initListener());
	}

	@Override
	public String getId() {
		return "menu.commands";
	}
}
