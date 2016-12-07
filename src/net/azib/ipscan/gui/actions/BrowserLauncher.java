/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.core.UserErrorException;

import java.awt.*;
import java.net.URI;

/**
 * The cross-platform browser launcher
 * 
 * @author Anton Keks
 */
public class BrowserLauncher {
	/**
	 * Opens an URL in the default browser.
	 * Supports Linux/Unix, MacOS, and Windows
	 * @param url
	 */
	public static void openURL(String url) {
		try {
      		Desktop.getDesktop().browse(new URI(url));
		}
		catch (Exception e) {
			throw new UserErrorException("openURL.failed", url);
		}
	}
}
