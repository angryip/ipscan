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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.GettingStartedDialog;
import net.azib.ipscan.gui.StatusBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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
		private AboutDialog aboutDialog;

		public About(AboutDialog aboutDialog) {
			super();
			this.aboutDialog = aboutDialog;
		}

		public void handleEvent(Event event) { 
			aboutDialog.open(); 
		}
	}

	public static final class Website implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.WEBSITE);
		}
	}

	public static final class FAQ implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.FAQ_URL);
		}
	}

	public static final class Plugins implements Listener { 		
		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.PLUGINS_URL);
		}
	}
	
	public static final class CheckVersion implements Listener {
		private final StatusBar statusBar;
		
		public CheckVersion(StatusBar statusBar) {
			this.statusBar = statusBar;
		}

		public void handleEvent(final Event event) {
			check();
		}
		
		public void check() {
			statusBar.setStatusText(Labels.getLabel("state.retrievingVersion"));
			
			// prepare message box in advance
			final MessageBox messageBox = new MessageBox(statusBar.getShell(), SWT.ICON_INFORMATION);
			messageBox.setText(Version.getFullName());

			Runnable checkVersionCode = new Runnable() {
				public void run() {
					BufferedReader reader = null;
					String message = null;
					try {
						URL url = new URL(Version.LATEST_VERSION_URL);
						URLConnection conn = url.openConnection();
						reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						
						String latestVersion = reader.readLine();
						latestVersion = latestVersion.substring(latestVersion.indexOf(' ')+1);
						
						if (!Version.getVersion().equals(latestVersion)) {
							message = Labels.getLabel("text.version.old");
							message = message.replaceFirst("%LATEST", latestVersion);
							message = message.replaceFirst("%VERSION", Version.getVersion());
						}
						else {
							message = Labels.getLabel("text.version.latest");
						}
					}
					catch (Exception e) {
						message = Labels.getLabel("exception.UserErrorException.version.latestFailed");
						Logger.getLogger(getClass().getName()).log(Level.WARNING, message, e);
					}
					finally {
						try {
							if (reader != null)
								reader.close();
						}
						catch (IOException e) {}
						
						// show the box in the SWT thread
						final String messageToShow = message;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								statusBar.setStatusText(null);
								messageBox.setMessage(messageToShow);
								messageBox.open();
							}
						});
					}
				}
			};
			new Thread(checkVersionCode).start();
		}
	}
}
