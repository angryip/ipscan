/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan;

import net.azib.ipscan.config.*;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.gui.InfoDialog;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.util.GoogleAnalytics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.security.Security;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.azib.ipscan.config.Labels.getLabel;

/**
 * The main executable class.
 * It initializes all the needed stuff and launches the user interface.
 * <p/>
 * All Exceptions, which are thrown out of the program, are caught and logged
 * using the java.util.logging facilities.
 * 
 * @see #main(String...)
 * @author Anton Keks
 */
public class Main {
	static final Logger LOG = LoggerFactory.getLogger();

	/**
	 * The launcher
	 * <p/>
	 * In development, pass the following on the JVM command line:
	 * <tt>-Djava.util.logging.config.file=config/logging.properties</tt>
	 * <p/>
	 * On Mac, add the following (otherwise SWT won't work):
	 * <tt>-XstartOnFirstThread</tt>
	 */
	public static void main(String... args) {
		MainWindow mainWindow = null;
		try {
			long startTime = System.currentTimeMillis();
			initSystemProperties();

			// this defines the Window class and app name on the Mac
			Display.setAppName(Version.NAME);
			Display display = Display.getDefault();
			LOG.finer("SWT initialized after " + (System.currentTimeMillis() - startTime));

			Locale locale = Config.getConfig().getLocale();
			Labels.initialize(locale);

			LOG.finer("Labels and Config initialized after " + (System.currentTimeMillis() - startTime));

			MainComponent mainComponent = DaggerMainComponent.create();
			if (Platform.MAC_OS) mainComponent.createMacApplicationMenu();
			LOG.finer("Components initialized after " + (System.currentTimeMillis() - startTime));

			processCommandLine(args, mainComponent);

			// create the main window using dependency injection
			mainWindow = mainComponent.createMainWindow();
			LOG.fine("Startup time: " + (System.currentTimeMillis() - startTime));

			while (!mainWindow.isDisposed()) {
				try {
					if (!display.readAndDispatch())
						display.sleep();
				}
				catch (Exception e) {
					if (e instanceof SWTException && e.getCause() instanceof Exception)
						e = (Exception) e.getCause();

					String localizedMessage = getLocalizedMessage(e);
					showMessage(mainWindow,
							e instanceof UserErrorException ? SWT.ICON_WARNING : SWT.ICON_ERROR,
							getLabel(e instanceof UserErrorException ? "text.userError" : "text.error"), localizedMessage);
				}
			}

			Config.getConfig().store();
			display.dispose();
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			new GoogleAnalytics().report(e);
			swingErrorDialog("Failed to load native code: " + e.getMessage() + "\nProbably you are using a binary built for wrong OS or CPU. If 64-bit binary doesn't work for you, try 32-bit version, or vice versa.");
		}
		catch (SWTError e) {
			if (e.getMessage().contains("gtk_init_check")) {
				System.err.println(e.toString() + " - probably you are running as `root` and/or don't have access to the X Server. Please run as normal user or with sudo.");
				new GoogleAnalytics().report(e);
			}
			else
				handleFatalError(mainWindow, e);
		}
		catch (Throwable e) {
			handleFatalError(mainWindow, e);
		}
	}

	private static void handleFatalError(MainWindow mainWindow, Throwable e) {
		e.printStackTrace();
		new GoogleAnalytics().report(e);
		showMessage(mainWindow, 0, "Fatal Error", e + "\nPlease submit a bug report mentioning your OS and what were you doing.");
	}

	private static void swingErrorDialog(String message) {
		try {
			Class.forName("javax.swing.JOptionPane").getMethod("showMessageDialog", Class.forName("java.awt.Component"), Object.class)
				.invoke(null, null, message);
		}
		catch (Exception e) {
			System.err.println(e.toString());
			System.err.println(message);
		}
	}

	private static void showMessage(MainWindow mainWindow, int flags, String title, String localizedMessage) {
		try {
			Shell parent = Display.getDefault().getActiveShell();
			if (parent == null) parent = mainWindow.getShell();
			MessageBox messageBox = new MessageBox(parent, SWT.OK | SWT.SHEET | flags);
			messageBox.setText(title);
			messageBox.setMessage(localizedMessage);
			messageBox.open();
		}
		catch (Throwable e) {
			new GoogleAnalytics().report(localizedMessage, e);
			swingErrorDialog(localizedMessage);
		}
	}

	private static void initSystemProperties() {
		// disable DNS caches
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	private static void processCommandLine(String[] args, MainComponent mainComponent) {
		if (args.length != 0) {
			CommandLineProcessor cli = mainComponent.createCommandLineProcessor();
			try {
				cli.parse(args);
			}
			catch (Exception e) {
				showMessageToConsole(e.getMessage() + "\n\n" + cli);
				System.exit(1);
			}
		}
	}

	private static void showMessageToConsole(String usageText) {
		// check if console is attached to the process
		if (System.console() != null) {
			System.err.println(usageText);
		}
		else {
			InfoDialog dialog = new InfoDialog(Version.NAME, getLabel("title.commandline"));
			dialog.setMessage(usageText);
			dialog.open();
		}
	}

	/**
	 * Returns a nice localized message for the passed exception
	 * in case it is possible, or toString() otherwise.
	 */
	static String getLocalizedMessage(Throwable e) {
		String localizedMessage;
		try {
			// try to load localized message
			if (e instanceof UserErrorException) {
				localizedMessage = e.getMessage();
			}
			else {
				String exceptionClassName = e.getClass().getSimpleName();
				String originalMessage = e.getMessage();
				localizedMessage = getLabel("exception." + exceptionClassName + (originalMessage != null ? "." + originalMessage : ""));
			}
			// add cause summary, if it exists
			if (e.getCause() != null) {
				localizedMessage += "\n\n" + e.getCause().toString();
			}
			LOG.log(Level.FINE, "error", e);
		}
		catch (Exception e2) {
			// fallback to default text
			localizedMessage = e.toString();
			// output stack trace to the console
			LOG.log(Level.SEVERE, "unexpected error", e);
		}
		return localizedMessage;
	}	
}
