package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.actions.HelpMenuActions.CheckVersion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

/**
 * Mac-specific application menu handler
 * in order to conform better to Mac standards.
 */
public class MacApplicationMenu {
	private AboutDialog aboutDialog;
	private PreferencesDialog preferencesDialog;
	private SelectFetchersDialog selectFetchersDialog;
	private CheckVersion checkVersion;

	public MacApplicationMenu(Display display, AboutDialog aboutDialog, PreferencesDialog preferencesDialog, SelectFetchersDialog selectFetchersDialog, CheckVersion checkVersion) {
		this.aboutDialog = aboutDialog;
		this.preferencesDialog = preferencesDialog;
		this.selectFetchersDialog = selectFetchersDialog;
		this.checkVersion = checkVersion;
		display.syncExec(() -> initApplicationMenu(display));
	}

	private void initApplicationMenu(Display display) {
		var systemMenu = display.getSystemMenu();
		if (systemMenu == null) return;

		var prefs = getItem(systemMenu, SWT.ID_PREFERENCES);
		if (prefs != null) prefs.addSelectionListener(widgetSelectedAdapter(e -> preferencesDialog.open()));

		var about = getItem(systemMenu, SWT.ID_ABOUT);
		if (about != null) about.addSelectionListener(widgetSelectedAdapter(e -> aboutDialog.open()));

		var fetchers = new MenuItem(systemMenu, SWT.NONE, systemMenu.indexOf(prefs) + 1);
		fetchers.setText(Labels.getLabel("menu.tools.fetchers"));
		fetchers.addSelectionListener(widgetSelectedAdapter(e -> selectFetchersDialog.open()));

		var checkVersion = new MenuItem(systemMenu, SWT.NONE, systemMenu.indexOf(about) + 1);
		checkVersion.setText(Labels.getLabel("menu.help.checkVersion"));
		checkVersion.addSelectionListener(widgetSelectedAdapter(e -> MacApplicationMenu.this.checkVersion.check(true)));
	}

	private static MenuItem getItem(Menu menu, int id) {
		var items = menu.getItems();
		for (var item : items) {
			if (item.getID() == id) return item;
		}
		return null;
	}
}
