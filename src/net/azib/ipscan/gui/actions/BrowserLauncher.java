/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.lang.reflect.Method;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.UserErrorException;

/**
 * The cross-platform browser launcher
 * 
 * @author anton
 */
public class BrowserLauncher {
	
	/**
	 * Opens an URL in the default browser.
	 * Supports Linux/Unix, MacOS, and Windows
	 * @param url
	 */
	public static void openURL(String url) {
		try {
			if (Platform.WINDOWS) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else
			if (Platform.MAC_OS) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			}
			else { // assume Linux or other Unix
				// TODO: what if browser is already running as another user, not root?
				String[] browsers = { "htmlview", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new Exception("Could not find web browser");
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
		catch (Exception e) {
			throw new UserErrorException("openURL.failed", url);
		}
	}
}
