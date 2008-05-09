/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.fetchers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.azib.ipscan.fetchers.HTTPSenderFetcher;
import net.azib.ipscan.gui.AbstractModalDialog;
import net.azib.ipscan.gui.util.LayoutHelper;

/**
 * HTTPSenderFetcherPrefs
 *
 * @author Anton Keks
 */
public class HTTPSenderFetcherPrefs extends AbstractModalDialog implements Runnable {
	
	private HTTPSenderFetcher fetcher;
	
	public HTTPSenderFetcherPrefs(HTTPSenderFetcher fetcher) {
		this.fetcher = fetcher;
	}
	
	@Override
	protected void populateShell() {
		shell = new Shell(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM);
		shell.setText(fetcher.getName());
		shell.setLayout(LayoutHelper.formLayout(3, 3, 3));
		
		Label label = new Label(shell, SWT.NONE);
		label.setText(fetcher.getTextToSend());
		
		shell.pack();
	}

	public void run() {
		open();
	}

}
