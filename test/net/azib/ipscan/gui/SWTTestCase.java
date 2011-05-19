/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;

/**
 * SWTTestCase - base class for SWT tests
 *
 * @author Anton Keks
 */
public abstract class SWTTestCase {
	protected static final Display display = Display.getDefault();

	protected Shell shell;
	
	@Before
	public void setUp() {
		newShell();
	}
	
	protected void newShell() {
		disposeShell();
		shell = new Shell(display);		
	}
	
	private void disposeShell() {
		if (shell != null) {
			shell.dispose();
			shell = null;
		}
	}
	
	@After
	public void tearDown() {
		display.syncExec(new Runnable() {
			public void run() {
				disposeShell();
			}
		});
	}
}
