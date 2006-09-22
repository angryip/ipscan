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
import net.azib.ipscan.gui.AboutWindow;
import net.azib.ipscan.gui.GettingStartedWindow;
import net.azib.ipscan.gui.MainWindow;
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
			new GettingStartedWindow().open();
		}
	}

	public static class About implements Listener { 		
		public void handleEvent(Event event) { 
			new AboutWindow().open(); 
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
		private MainWindow mainWindow;
		
		public CheckVersion(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}

		public void handleEvent(Event event) {
			BufferedReader reader = null;
			try {
				mainWindow.setStatusText(Labels.getInstance().getString("state.retrievingVersion"));
				
				URL url = new URL(Version.LATEST_VERSION_URL);
				URLConnection conn = url.openConnection();
				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				String latestVersion = reader.readLine();
				latestVersion = latestVersion.substring(latestVersion.indexOf(' ')+1);
				
				reader.close();
				
				MessageBox messageBox = new MessageBox(event.display.getActiveShell(), SWT.ICON_INFORMATION);
				messageBox.setText(Version.FULL_NAME);
				
				if (!Version.VERSION.equals(latestVersion)) {
					String message = Labels.getInstance().getString("text.version.old");
					message = message.replaceFirst("%LATEST", latestVersion);
					message = message.replaceFirst("%VERSION", Version.VERSION);
					messageBox.setMessage(message);
				}
				else {
					messageBox.setMessage(Labels.getInstance().getString("text.version.latest"));
				}
				messageBox.open();
			}
			catch (Exception e) {
				throw new UserErrorException("version.latestFailed", e);
			}
			finally {
				try {
					reader.close();
				}
				catch (IOException e) {}
				
				mainWindow.setStatusText(null);
			}
		}
	}
}
