/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.io.IOException;

import net.azib.ipscan.gui.UserErrorException;

/**
 * OpenerLauncher
 *
 * @author anton
 */
public class OpenerLauncher {
	
	public void launch(String openerString) {
		// check for URLs
		if (openerString.startsWith("http:") || openerString.startsWith("https:") || openerString.startsWith("ftp:") || openerString.startsWith("mailto:")) {
			BrowserLauncher.openURL(openerString);
		}
		else {
			// run a process here
			try {
				// TODO: we probably need to support shell patterns, etc
				Runtime.getRuntime().exec(openerString);
			}
			catch (IOException e) {
				throw new UserErrorException("opener.failed", openerString);
			}
		}

	}

}
