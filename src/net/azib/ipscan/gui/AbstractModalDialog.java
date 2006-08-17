/**
 * 
 */
package net.azib.ipscan.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This is the base of a modal dialog window
 *
 * @author anton
 */
public abstract class AbstractModalDialog {

	protected Shell shell = null;

	public void open() {
		shell.open();
		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		shell.dispose();
	}

}
