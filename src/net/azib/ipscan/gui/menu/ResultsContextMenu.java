package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.DefaultOpenerConfig;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.actions.CommandsMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ResultsContextMenu extends CommandsMenu {
	public ResultsContextMenu(Shell parent, CommandsMenuActions actions, OpenersMenu openersMenu,
							  OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig, ResultTable resultTable) {
		super(parent, SWT.POP_UP);
		build(actions, openersMenu, openersConfig, defaultOpenerConfig, resultTable);
	}
}
