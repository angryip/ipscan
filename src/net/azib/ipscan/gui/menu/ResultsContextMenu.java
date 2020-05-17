package net.azib.ipscan.gui.menu;

import net.azib.ipscan.di.Inject;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ResultsContextMenu extends CommandsMenu {

	@Inject public ResultsContextMenu(Shell parent, CommandsMenuActions actions, OpenersMenu openersMenu) {
		super(parent, SWT.POP_UP, actions, openersMenu);
	}
}
