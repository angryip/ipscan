package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.di.Injector;
import net.azib.ipscan.util.GoogleAnalytics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.azib.ipscan.config.Labels.getLabel;

public class GUI implements AutoCloseable {
	static final Logger LOG = LoggerFactory.getLogger();
	private long startTime = System.currentTimeMillis();
	private Display display;
	private MainWindow mainWindow;

	public GUI() {
		try {
			// this defines the Window class and app name on the Mac
			Display.setAppName(Version.NAME);
			display = Display.getDefault();
			LOG.finer("SWT initialized after " + (System.currentTimeMillis() - startTime));
		}
		catch (SWTError e) {
			if (e.getMessage().contains("gtk_init_check")) {
				System.err.println(e.toString() + ": probably you are running as `root` and/or don't have access to the X Server. Please run as normal user or with sudo.");
				new GoogleAnalytics().report(e);
			}
			else if (e.getMessage().contains("Invalid thread access")) {
				System.err.println(e.toString() + ": you need to start Java with -XstartOnFirstThread on a Mac");
				new GoogleAnalytics().report(e);
			}
			else throw e;
		}
	}

	public void showMainWindow(Injector injector, boolean showStartupInfo) {
		mainWindow = injector.require(MainWindow.class);
		LOG.fine("Main window created: " + (System.currentTimeMillis() - startTime));

		if (showStartupInfo)
			injector.require(Startup.class).onStart();

		while (!mainWindow.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			}
			catch (Exception e) {
				if (e instanceof SWTException && e.getCause() instanceof Exception)
					e = (Exception) e.getCause();

				var localizedMessage = getLocalizedMessage(e);
				showMessage(e instanceof UserErrorException ? SWT.ICON_WARNING : SWT.ICON_ERROR,
					getLabel(e instanceof UserErrorException ? "text.userError" : "text.error"), localizedMessage);

				if (!(e instanceof UserErrorException) || e.getCause() != null)
					new GoogleAnalytics().report(e);
			}
		}
	}

	public void showMessage(int flags, String title, String localizedMessage) {
		var parent = Display.getDefault().getActiveShell();
		if (parent == null && mainWindow != null) parent = mainWindow.getShell();
		if (parent == null || parent.isDisposed()) parent = new Shell();
		var messageBox = new MessageBox(parent, SWT.OK | SWT.SHEET | flags);
		messageBox.setText(title);
		messageBox.setMessage(localizedMessage);
		messageBox.open();
	}

	@Override public void close() {
		try {
			display.dispose();
		}
		catch (SWTException ignore) {}
	}

	/**
	 * Returns a nice localized message for the passed exception
	 * in case it is possible, or toString() otherwise.
	 */
	static String getLocalizedMessage(Throwable e) {
		var exceptionClassName = e.getClass().getSimpleName();
		var originalMessage = e.getMessage();
		var localizedMessage = Labels.getInstance().getOrNull("exception." + exceptionClassName + (originalMessage != null ? "." + originalMessage : ""));
		if (localizedMessage == null) {
			// fallback to default text
			localizedMessage = e.toString();
			// output stack trace to the console
			LOG.log(Level.SEVERE, "unexpected error", e);
		} else {
			// add cause summary, if it exists
			if (e.getCause() != null) {
				localizedMessage += "\n\n" + e.getCause().toString();
			}
			LOG.log(Level.FINE, "error", e);
		}
		return localizedMessage;
	}
}
