package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Labels;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

class ExtendableMenu extends Menu {

	public ExtendableMenu(Shell parent, int style) {
		super(parent, style);
	}

	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener) {
		return initMenuItem(parent, label, acceleratorText, accelerator, listener, false);
	}

	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener, boolean disableDuringScanning) {
		MenuItem menuItem = new MenuItem(parent, label == null ? SWT.SEPARATOR : SWT.PUSH);

		if (label != null)
			menuItem.setText(Labels.getLabel(label) + (acceleratorText != null ? "\t" + acceleratorText : ""));

		if (accelerator != null)
			menuItem.setAccelerator(accelerator);

		if (listener != null)
			menuItem.addListener(SWT.Selection, listener);
		else
			menuItem.setEnabled(false);

		if (disableDuringScanning) {
			menuItem.setData("disableDuringScanning", true);
		}

		return menuItem;
	}

	@Override
	protected void checkSubclass() { } // allow extending of Menu class
}
