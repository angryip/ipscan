/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.CommandLineProcessor;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.AboutDialog;
import net.azib.ipscan.gui.GettingStartedDialog;
import net.azib.ipscan.gui.InfoDialog;
import net.azib.ipscan.gui.StatusBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HelpActions
 *
 * @author Anton Keks
 */
public class HelpMenuActions {

	public static final class GettingStarted implements Listener {
		@Inject
		public GettingStarted() {}

		public void handleEvent(Event event) {
			new GettingStartedDialog().open();
		}
	}

	public static final class CommandLineUsage implements Listener {
		private CommandLineProcessor cli;

		@Inject
		public CommandLineUsage(CommandLineProcessor cli) {
			this.cli = cli;
		}

		public void handleEvent(Event event) {
			InfoDialog dialog = new InfoDialog(Version.NAME, Labels.getLabel("title.commandline"));
			dialog.setMessage(cli.toString());
			dialog.open();
		}
	}

	public static final class About implements Listener {
		private AboutDialog aboutDialog;

		@Inject public About(AboutDialog aboutDialog) {
			this.aboutDialog = aboutDialog;
		}

		public void handleEvent(Event event) { 
			aboutDialog.open(); 
		}
	}

	public static final class Website implements Listener {
		@Inject
		public Website() {}

		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.WEBSITE);
		}
	}

	public static final class FAQ implements Listener {
		@Inject
		public FAQ() {}

		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.FAQ_URL);
		}
	}

	public static final class Plugins implements Listener {
		@Inject
		public Plugins() {}

		public void handleEvent(Event event) {
			BrowserLauncher.openURL(Version.PLUGINS_URL);
		}
	}
	
	public static final class CheckVersion implements Listener {
		private final StatusBar statusBar;
		
		@Inject public CheckVersion(StatusBar statusBar) {
			this.statusBar = statusBar;
		}

		public void handleEvent(final Event event) {
			check();
		}
		
		public void check() {
			statusBar.setStatusText(Labels.getLabel("state.retrievingVersion"));
			
			Runnable checkVersionCode = new Runnable() {
				public void run() {
					BufferedReader reader = null;
					String message = null;
					int messageStyle = SWT.ICON_INFORMATION;
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
							messageStyle = SWT.ICON_QUESTION | SWT.YES | SWT.NO;
						}
						else {
							message = Labels.getLabel("text.version.latest");
							messageStyle = SWT.ICON_INFORMATION;
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
						final int messageStyleToShow = messageStyle;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								statusBar.setStatusText(null);
								MessageBox messageBox = new MessageBox(statusBar.getShell(), messageStyleToShow | SWT.SHEET);
								messageBox.setText(Version.getFullName());
								messageBox.setMessage(messageToShow);
								if (messageBox.open() == SWT.YES) {
									BrowserLauncher.openURL(Version.DOWNLOAD_URL);
								}
							}
						});
					}
				}
			};
			new Thread(checkVersionCode).start();
		}
	}
}
