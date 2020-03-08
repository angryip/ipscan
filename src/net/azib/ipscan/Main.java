/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan;

import net.azib.ipscan.config.*;
import net.azib.ipscan.gui.GUI;
import net.azib.ipscan.gui.InfoDialog;
import net.azib.ipscan.util.GoogleAnalytics;

import java.security.Security;
import java.util.Locale;
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
		GUI gui = null;
		try {
			long startTime = System.currentTimeMillis();
			gui = new GUI();
			disableDNSCache();

			Locale locale = Config.getConfig().getLocale();
			Labels.initialize(locale);
			LOG.finer("Labels and Config initialized after " + (System.currentTimeMillis() - startTime));

			MainComponent mainComponent = DaggerMainComponent.create();
			if (Platform.MAC_OS) mainComponent.createMacApplicationMenu();
			LOG.finer("Components initialized after " + (System.currentTimeMillis() - startTime));

			processCommandLine(args, mainComponent);

			gui.showMainWindow(mainComponent);

			Config.getConfig().store();
			gui.close();
		}
		catch (UnsatisfiedLinkError e) {
			e.printStackTrace();
			new GoogleAnalytics().report(e);
			swingErrorDialog("Failed to load native code: " + e.getMessage() + "\n\nProbably you are using a binary built for wrong OS or CPU. If 64-bit binary doesn't work for you, try 32-bit version, or vice versa.");
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			new GoogleAnalytics().report(e);
			swingErrorDialog("SWT GUI toolkit not available: " + e.toString() + "\n\nIf you are using platform-neutral build, make sure you provide SWT built for your platform manually (e.g. install libswt packages), or please use a platform specific binary.");
		}
		catch (Throwable e) {
			handleFatalError(gui, e);
		}
	}

	private static void handleFatalError(GUI gui, Throwable e) {
		e.printStackTrace();
		new GoogleAnalytics().report(e);
		if (gui != null)
			gui.showMessage(0, "Fatal Error", e + "\nPlease submit a bug report mentioning your OS and what exactly were you doing.");
		else
			swingErrorDialog(e.getMessage());
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

	private static void disableDNSCache() {
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
}
