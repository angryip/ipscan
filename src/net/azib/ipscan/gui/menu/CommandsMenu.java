package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.MACFetcher;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class CommandsMenu extends AbstractMenu {
	private final CommandsMenuActions actions;
	private final FetcherRegistry fetcherRegistry;
	private final int copyMACIndex;
	private MenuItem copyMACItem;

	public CommandsMenu(Shell parent, CommandsMenuActions actions, OpenersMenu openersMenu, FetcherRegistry fetcherRegistry) {
		this(parent, SWT.DROP_DOWN, actions, openersMenu, fetcherRegistry);
	}

	protected CommandsMenu(Shell parent, int style, CommandsMenuActions actions, OpenersMenu openersMenu, FetcherRegistry fetcherRegistry) {
		super(parent, style);
		this.actions = actions;
		this.fetcherRegistry = fetcherRegistry;

		initMenuItem(this, "menu.commands.details", null, null, actions.details);
		initMenuItem(this, null, null, null, null);
		// not disabled during scanning: Ctrl+R can add more IPs to a rescan in progress
		initMenuItem(this, "menu.commands.rescan", "Ctrl+R", SWT.MOD1 | 'R', actions.rescan);
		initMenuItem(this, "menu.commands.delete", Platform.MAC_OS ? "⌦" : "Del", /* this is not a global key binding */ null, actions.delete, true);
		initMenuItem(this, null, null, null, null);
		initMenuItem(this, "menu.commands.copy", Platform.MAC_OS ? "⌘C" : "Ctrl+C", /* this is not a global key binding */ null, actions.copyIP);
		copyMACIndex = getItems().length;
		initMenuItem(this, "menu.commands.copyDetails", null, null, actions.copyIPDetails);
		initMenuItem(this, null, null, null, null);

		var openersMenuItem = new MenuItem(this, SWT.CASCADE);
		openersMenuItem.setText(Labels.getLabel(openersMenu.getId()));
		openersMenuItem.setMenu(openersMenu);

		// initMenuItem(subMenu, "menu.commands.show", null, initListener());

		addListener(SWT.Show, event -> updateCopyMACItem());
		updateCopyMACItem();
	}

	private void updateCopyMACItem() {
		var macFetcherActive = fetcherRegistry.getSelectedFetcherIndex(MACFetcher.ID) >= 0;
		if (macFetcherActive && copyMACItem == null) {
			copyMACItem = new MenuItem(this, SWT.PUSH, copyMACIndex);
			copyMACItem.setText(Labels.getLabel("menu.commands.copyMac"));
			copyMACItem.addListener(SWT.Selection, actions.copyMAC);
		}
		else if (!macFetcherActive && copyMACItem != null) {
			copyMACItem.dispose();
			copyMACItem = null;
		}
	}

	@Override
	public String getId() {
		return "menu.commands";
	}
}
