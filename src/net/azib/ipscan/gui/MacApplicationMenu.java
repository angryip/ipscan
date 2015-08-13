package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.actions.HelpMenuActions.CheckVersion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.picocontainer.Startable;

import javax.inject.Inject;

/**
 * Mac-specific application menu handler
 * in order to conform better to Mac standards.
 */
public class MacApplicationMenu implements Startable {
	@Inject
	AboutDialog aboutDialog;

	private final PreferencesDialog preferencesDialog;
	private final SelectFetchersDialog selectFetchersDialog;
	private final CheckVersion checkVersionListener;

	public MacApplicationMenu(PreferencesDialog preferencesDialog, SelectFetchersDialog selectFetchersDialog, CheckVersion checkVersionListener) {
		this.preferencesDialog = preferencesDialog;
		this.selectFetchersDialog = selectFetchersDialog;
		this.checkVersionListener = checkVersionListener;
	}

	public void start() {
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				initApplicationMenu(display);
			}
		});
	}

	public void stop() {
	}

	void initApplicationMenu(Display display) {
		Menu systemMenu = display.getSystemMenu();
		if (systemMenu == null) return;

		MenuItem prefs = getItem(systemMenu, SWT.ID_PREFERENCES);
		prefs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				preferencesDialog.open();
			}
		});

		MenuItem about = getItem(systemMenu, SWT.ID_ABOUT);
		// about.setText(Labels.getLabel("title.about") + " " + Version.NAME);
		about.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				aboutDialog.open();
			}
		});

		MenuItem fetchers = new MenuItem(systemMenu, SWT.NONE, systemMenu.indexOf(prefs) + 1);
		fetchers.setText(Labels.getLabel("menu.tools.fetchers"));
		fetchers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectFetchersDialog.open();
			}
		});

		MenuItem checkVersion = new MenuItem(systemMenu, SWT.NONE, systemMenu.indexOf(about) + 1);
		checkVersion.setText(Labels.getLabel("menu.help.checkVersion"));
		checkVersion.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkVersionListener.check();
			}
		});
	}

	static MenuItem getItem(Menu menu, int id) {
		MenuItem[] items = menu.getItems();
		for (MenuItem item : items) {
			if (item.getID() == id) return item;
		}
		return null;
	}
}
