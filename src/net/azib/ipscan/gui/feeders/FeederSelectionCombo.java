package net.azib.ipscan.gui.feeders;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class FeederSelectionCombo extends Combo {
	public FeederSelectionCombo(Composite parent) {
		super(parent, SWT.READ_ONLY);
	}

	@Override protected void checkSubclass() {}
}
