/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.GettingStartedDialog;
import net.azib.ipscan.gui.StatusBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

/**
 * HelpActions
 *
 * @author Anton Keks
 */
public class HelpActions {
	
	public static final class GettingStarted implements Listener {
		public void handleEvent(Event event) {
			new GettingStartedDialog().open();
		}
	}

	public static final class About implements Listener { 		
		public void handleEvent(Event event) { 
			new AboutDialog().open(); 
		}
	}

	public static final class Website implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.WEBSITE);
		}
	}

	public static final class Forum implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.FORUM_URL);
		}
	}

	public static final class Plugins implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.PLUGINS_URL);
		}
	}
	
	public static final class CheckVersion implements Listener {
		private StatusBar statusBar;
		
		public CheckVersion(StatusBar statusBar) {
			this.statusBar = statusBar;
		}

		public void handleEvent(Event event) {
			BufferedReader reader = null;
			try {
				statusBar.setStatusText(Labels.getLabel("state.retrievingVersion"));
				
				URL url = new URL(Version.LATEST_VERSION_URL);
				URLConnection conn = url.openConnection();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				String latestVersion = reader.readLine();
				latestVersion = latestVersion.substring(latestVersion.indexOf(' ')+1);
				
				reader.close();
				
				MessageBox messageBox = new MessageBox(event.display.getActiveShell(), SWT.ICON_INFORMATION);
				messageBox.setText(Version.getFullName());
				
				if (!Version.getVersion().equals(latestVersion)) {
					String message = Labels.getLabel("text.version.old");
					message = message.replaceFirst("%LATEST", latestVersion);
					message = message.replaceFirst("%VERSION", Version.getVersion());
					messageBox.setMessage(message);
				}
				else {
					messageBox.setMessage(Labels.getLabel("text.version.latest"));
				}
				messageBox.open();
			}
			catch (Exception e) {
				throw new UserErrorException("version.latestFailed", e);
			}
			finally {
				try {
					if (reader != null)
						reader.close();
				}
				catch (IOException e) {}
				
				statusBar.setStatusText(null);
			}
		}
	}
}
