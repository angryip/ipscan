/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.core.UserErrorException;
import org.eclipse.swt.program.Program;

public class BrowserLauncher {
	/**
	 * Opens an URL in the default browser.
	 * @param url
	 */
	public static void openURL(String url) {
		if (!Program.launch(url)) throw new UserErrorException("openURL.failed", url);
	}
}
