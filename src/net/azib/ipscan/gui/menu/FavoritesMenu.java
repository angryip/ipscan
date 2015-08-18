package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.FavoritesMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * FavoritesMenu wrapper for type-safety
 */
@Singleton
public class FavoritesMenu extends AbstractMenu {

	@Inject
	public FavoritesMenu(Shell parent,
						 FavoritesMenuActions.Add addListener,
						 FavoritesMenuActions.Edit editListener,
						 FavoritesMenuActions.ShowMenu showFavoritesMenuListener) {

		super(parent);

		initMenuItem(this, "menu.favorites.add", "Ctrl+D", SWT.MOD1 | 'D', addListener);
		initMenuItem(this, "menu.favorites.edit", null, null, editListener);
		initMenuItem(this, null, null, null, null);

		addListener(SWT.Show, showFavoritesMenuListener);
	}

	@Override
	public String getId() {
		return "menu.favorites";
	}
}
