package net.azib.ipscan.gui;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Platform;
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

import java.lang.reflect.Method;
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
				System.err.println(e.toString() + " - probably you are running as `root` and/or don't have access to the X Server. Please run as normal user or with sudo.");
				new GoogleAnalytics().report(e);
			}
			else throw e;
		}
	}

	public void showMainWindow(Injector injector) {
		mainWindow = injector.require(MainWindow.class);
		if (Platform.MAC_OS) setMacDarkAppearanceIfNeeded();

		LOG.fine("Main window created: " + (System.currentTimeMillis() - startTime));

		while (!mainWindow.isDisposed()) {
			try {
				if (!display.readAndDispatch())
					display.sleep();
			}
			catch (Exception e) {
				if (e instanceof SWTException && e.getCause() instanceof Exception)
					e = (Exception) e.getCause();

				String localizedMessage = getLocalizedMessage(e);
				showMessage(e instanceof UserErrorException ? SWT.ICON_WARNING : SWT.ICON_ERROR,
						getLabel(e instanceof UserErrorException ? "text.userError" : "text.error"), localizedMessage);
			}
		}
	}

	private void setMacDarkAppearanceIfNeeded() {
		try {
			// changing the appearance works only after the shell has been created
			Class os = Class.forName("org.eclipse.swt.internal.cocoa.OS");
			Boolean isDarkMode = (Boolean) os.getMethod("isSystemDarkAppearance").invoke(null);
			Boolean isAppDarkAppearance = (Boolean) os.getMethod("isAppDarkAppearance").invoke(null);
			LOG.info("Dark appearance flags before: " + isDarkMode + ", " + isAppDarkAppearance);
			if (isDarkMode && !isAppDarkAppearance) {
				os.getMethod("setTheme", boolean.class).invoke(null, true);
				isDarkMode = (Boolean) os.getMethod("isSystemDarkAppearance").invoke(null);
				isAppDarkAppearance = (Boolean) os.getMethod("isAppDarkAppearance").invoke(null);
				LOG.info("Dark appearance flags after: " + isDarkMode + ", " + isAppDarkAppearance);
				if (isAppDarkAppearance) {
					// workaround for a bug in SWT: colors need to be reinited after changing the appearance
					Method initColors = display.getClass().getDeclaredMethod("initColors");
					initColors.setAccessible(true);
					initColors.invoke(display);
					LOG.info("initColors called");
				}
				else {
					os.getMethod("setTheme", boolean.class).invoke(null, false);
					LOG.info("Dark appearance reset back because it didn't work");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMessage(int flags, String title, String localizedMessage) {
		Shell parent = Display.getDefault().getActiveShell();
		if (parent == null && mainWindow != null) parent = mainWindow.getShell();
		if (parent == null) parent = new Shell();
		MessageBox messageBox = new MessageBox(parent, SWT.OK | SWT.SHEET | flags);
		messageBox.setText(title);
		messageBox.setMessage(localizedMessage);
		messageBox.open();
		new GoogleAnalytics().report(localizedMessage, (Throwable) null);
	}

	@Override public void close() {
		display.dispose();
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
