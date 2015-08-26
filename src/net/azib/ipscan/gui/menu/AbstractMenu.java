package net.azib.ipscan.gui.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractMenu extends ExtendableMenu {

	public AbstractMenu(Shell parent, int style) {
		super(parent, style);
	}

	public AbstractMenu(Shell parent) {
		super(parent, SWT.DROP_DOWN);
	}

	public abstract String getId();
}
