/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.UserErrorException;

/**
 * The cross-platform browser launcher
 * 
 * @author Anton Keks
 */
public class BrowserLauncher {
	
	private static final String[] BROWSERS = {"htmlview", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
	private static String browser;
	
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
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				openURL.invoke(null, new Object[] { url });
			}
			else { // assume Linux or other Unix
				// TODO: what if browser is already running as another user, not root?
				
				if (browser == null) {
					// try to detect gnome default browser
					browser = execAndReturn("gconftool", "-g", "/desktop/gnome/applications/browser/exec");
					// fall back to searching for known browsers
					if (browser == null) {
						for (int count = 0; count < BROWSERS.length && browser == null; count++)
							if (Runtime.getRuntime().exec(new String[] {"which", BROWSERS[count]}).waitFor() == 0)
								browser = BROWSERS[count];
					}
					if (browser == null)
						throw new Exception("Could not find web browser");
				}
				Runtime.getRuntime().exec(new String[] {browser, url});
			}
		}
		catch (Exception e) {
			throw new UserErrorException("openURL.failed", url);
		}
	}
	
	private static String execAndReturn(String... exec) {
		try {
			Process p = Runtime.getRuntime().exec(exec);
			if (p.waitFor() == 0) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String result = reader.readLine();
				reader.close();
				return result;
			}
		}
		catch (Exception e) {
		}
		return null;
	}
}
