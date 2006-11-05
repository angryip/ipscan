/**
 * 
 */
package net.azib.ipscan;

import java.security.Security;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.GUIComponentContainer;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.UserErrorException;

/**
 * The main executable class.
 * It initializes all the needed stuff and launches the user interface.
 * 
 * All Exceptions, which are thrown out of the program, are catched and logged
 * using the java.util.logging facilities.
 * 
 * @author anton
 */
public class Main {

	public static void main(String[] args) {
		
		initProperties();
		
		Display display = Display.getDefault();

		// initalize Labels instance
		Labels.initialize(new Locale("en"));	// TODO: retrieve locale normally
		
		// initialize Config instance
		Config.initialize();
		
		// create the main window using dependency injection
		MainWindow mainWindow = new GUIComponentContainer().createMainWindow();
		
		while (!mainWindow.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			}
			catch (Throwable e) {
				// display a nice error message
				String localizedMessage = getLocalizedMessage(e);
				Shell parent = display.getActiveShell();
				MessageBox messageBox = new MessageBox(parent != null ? parent : mainWindow.getShell(), SWT.OK | SWT.ICON_ERROR);
				messageBox.setText("Error");
				messageBox.setMessage(localizedMessage);
				messageBox.open();
			}
		}
		
		// save config on exit
		Config.store();
		
		// dispose the native objects
		display.dispose();
	}

	private static void initProperties() {
		// currently we support IPv4 only
		System.setProperty("java.net.preferIPv4Stack", "true");
		// disable DNS caches
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
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
				String exceptionClassName = getClassShortName(e.getClass());
				String originalMessage = e.getMessage();
				localizedMessage = Labels.getLabel("exception." + exceptionClassName + (originalMessage != null ? "." + originalMessage : ""));
			}
			// add cause summary, if it exists
			if (e.getCause() != null) {
				localizedMessage += "\n\n" + e.getCause().toString();
			}
			Logger.global.log(Level.FINE, "error", e);
		}
		catch (Exception e2) {
			// fallback to default text
			localizedMessage = e.toString();
			// output stack trace to the console
			Logger.global.log(Level.SEVERE, "unexpected error", e);
		}
		return localizedMessage;
	}
	
	/**
	 * @return the short name of a class (without package name)
	 */
	static String getClassShortName(Class clazz) {
		String className = clazz.getName();
		return className.substring(className.lastIndexOf('.') + 1);
	}
	
}
