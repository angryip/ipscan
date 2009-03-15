/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan;

import java.security.Security;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.CommandLineProcessor;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.gui.InfoDialog;
import net.azib.ipscan.gui.MainWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
	 * The launching point
	 * <p/>
	 * In development, pass the following on the JVM command line:
	 * <tt>-Djava.util.logging.config.file=config/logging.properties</tt>
	 * <p/>
	 * On Mac, add the following (otherwise SWT won't work):
	 * <tt>-XstartOnFirstThread</tt>
	 */
	public static void main(String... args) {
		
		long startTime = System.currentTimeMillis();
		
		initSystemProperties();
		
		Display display = Display.getDefault();		
		LOG.finer("SWT initialized after " + (System.currentTimeMillis() - startTime));

		// initialize Labels instance
		Labels.initialize(Locale.getDefault());				
		// initialize Config instance
		Config globalConfig = Config.getConfig();		
		LOG.finer("Labels and Config initialized after " + (System.currentTimeMillis() - startTime));
		
		ComponentRegistry componentRegistry = new ComponentRegistry();
		LOG.finer("ComponentRegistry initialized after " + (System.currentTimeMillis() - startTime));
		
		processCommandLine(args, componentRegistry);
		
		// create the main window using dependency injection
		MainWindow mainWindow = componentRegistry.getMainWindow();		
		LOG.fine("Startup time: " + (System.currentTimeMillis() - startTime));
		
		while (!mainWindow.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			}
			catch (Throwable e) {
				// display a nice error message
				String localizedMessage = getLocalizedMessage(e);
				Shell parent = display.getActiveShell();
				showMessage(parent != null ? parent : mainWindow.getShell(), 
						e instanceof UserErrorException ? SWT.ICON_WARNING : SWT.ICON_ERROR, 
						Labels.getLabel(e instanceof UserErrorException ? "text.userError" : "text.error"), localizedMessage);
			}
		}
		
		// save config on exit
		globalConfig.store();
		
		// dispose the native objects
		display.dispose();
	}

	private static void showMessage(Shell parent, int flags, String title, String localizedMessage) {
		MessageBox messageBox = new MessageBox(parent, SWT.OK | flags);
		messageBox.setText(title);
		messageBox.setMessage(localizedMessage);
		messageBox.open();
	}

	private static void initSystemProperties() {
		// currently we support IPv4 only
		System.setProperty("java.net.preferIPv4Stack", "true");
		// disable DNS caches
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	private static void processCommandLine(String[] args, ComponentRegistry componentRegistry) {
		if (args.length != 0) {
			// TODO: implement command-line
			CommandLineProcessor cli = componentRegistry.getCommandLineProcessor();
			showMessageToConsole(cli.toString());
		}
	}

	/**
	 * @param usageText
	 */
	private static void showMessageToConsole(String usageText) {
		// use console for all platforms except Windows by default
		boolean haveConsole = !Platform.WINDOWS;
		
		try {
			// determine if we have console attached using Java 6 System.console()
			haveConsole = System.class.getMethod("console").invoke(null) != null;
		}
		catch (Exception e) {
			// Java 5 will reach here
		}
		
		if (haveConsole) {
			System.err.println(usageText);
		}
		else {
			InfoDialog dialog = new InfoDialog(Version.NAME, Labels.getLabel("title.commandline"));
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
				localizedMessage = Labels.getLabel("exception." + exceptionClassName + (originalMessage != null ? "." + originalMessage : ""));
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
