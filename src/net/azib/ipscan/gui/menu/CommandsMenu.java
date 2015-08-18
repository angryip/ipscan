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

	@Inject
	public CommandsMenu(Shell parent,
						CommandsMenuActions.Details details,
						CommandsMenuActions.Rescan rescan,
						CommandsMenuActions.Delete delete,
						CommandsMenuActions.CopyIP copyIP,
						CommandsMenuActions.CopyIPDetails copyIPDetails,
						OpenersMenu openersMenu) {

		super(parent);

		initMenuItem(this, "menu.commands.details", null, null, details);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.rescan", "Ctrl+R", SWT.MOD1 | 'R', rescan, true);
		initMenuItem(this, "menu.commands.delete", Platform.MAC_OS ? "?" : "Del", /* this is not a global key binding */ null, delete, true);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.copy", Platform.MAC_OS ? "?C" : "Ctrl+C", /* this is not a global key binding */ null, copyIP);
		initMenuItem(this, "menu.commands.copyDetails", null, null, copyIPDetails);
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
