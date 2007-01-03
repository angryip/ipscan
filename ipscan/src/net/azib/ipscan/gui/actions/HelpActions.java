/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.GettingStartedDialog;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

/**
 * HelpActions
 *
 * @author anton
 */
public class HelpActions {
	
	public static class GettingStarted implements Listener {
		public void handleEvent(Event event) {
			new GettingStartedDialog().open();
		}
	}

	public static class About implements Listener { 		
		public void handleEvent(Event event) { 
			new AboutDialog().open(); 
		}
	}

	public static class Website implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.WEBSITE);
		}
	}

	public static class Forum implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.FORUM_URL);
		}
	}

	public static class Plugins implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.PLUGINS_URL);
		}
	}
	
	public static class CheckVersion implements Listener {
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
				messageBox.setText(Version.FULL_NAME);
				
				if (!Version.VERSION.equals(latestVersion)) {
					String message = Labels.getLabel("text.version.old");
					message = message.replaceFirst("%LATEST", latestVersion);
					message = message.replaceFirst("%VERSION", Version.VERSION);
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
