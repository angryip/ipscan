package net.azib.ipscan.gui.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * CommandsMenu wrapper for type-safety
 */
@Singleton
public class ResultsContextMenu extends AbstractMenu {

	@Inject
	public ResultsContextMenu(Shell parent) {
		super(parent, SWT.POP_UP);
	}
}
