package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.ColumnsActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * ColumnsMenu wrapper for type-safety.
 * This is the menu when clicking on a column header.
 */
@Singleton
public class ColumnsMenu extends ExtendableMenu {

	@Inject
	public ColumnsMenu(Shell parent,
					   ColumnsActions.SortBy sortByListener,
					   ColumnsActions.AboutFetcher aboutListener,
					   ColumnsActions.FetcherPreferences preferencesListener) {

		super(parent, SWT.POP_UP);

		initMenuItem(this, "menu.columns.sortBy", null, null, sortByListener);
		initMenuItem(this, "menu.columns.preferences", null, null, preferencesListener);
		initMenuItem(this, "menu.columns.about", null, null, aboutListener);
	}
}
