package net.azib.ipscan.gui.menu;

import net.azib.ipscan.gui.actions.CommandsMenuActions.EditOpeners;
import net.azib.ipscan.gui.actions.CommandsMenuActions.ShowOpenersMenu;
import org.eclipse.swt.widgets.Shell;

public class OpenersContextMenu extends OpenersMenu {
	public OpenersContextMenu(Shell parent, EditOpeners editOpenersListener, ShowOpenersMenu showOpenersMenuListener) {
		super(parent, editOpenersListener, showOpenersMenuListener);
	}
}
