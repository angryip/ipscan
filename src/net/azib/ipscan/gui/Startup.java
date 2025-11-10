package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.actions.HelpMenuActions.CheckVersion;
import net.azib.ipscan.util.GoogleAnalytics;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Startup {
	private Shell shell;
	private GUIConfig guiConfig;
	private CheckVersion checkVersion;

	public Startup(Shell shell, GUIConfig guiConfig, CheckVersion checkVersion) {
		this.shell = shell;
		this.guiConfig = guiConfig;
		this.checkVersion = checkVersion;
	}

	public void onStart() {
		if (guiConfig.isFirstRun) {
			new GoogleAnalytics().asyncReport("First run");
			Display.getCurrent().asyncExec(() -> {
				var dialog = new GettingStartedDialog();
				if (Platform.CRIPPLED_WINDOWS)
					dialog.prependText(Labels.getLabel("text.crippledWindowsInfo"));

				shell.forceActive();
				dialog.open();
				guiConfig.isFirstRun = false;
				checkForLatestVersion();
			});
		}
		else if (!Version.getVersion().equals(guiConfig.lastRunVersion)) {
			new GoogleAnalytics().asyncReport("Update " + guiConfig.lastRunVersion + " to " + Version.getVersion());
			guiConfig.lastRunVersion = Version.getVersion();
		}
		else if (guiConfig.versionCheckEnabled && System.currentTimeMillis() - guiConfig.lastVersionCheck > 30L * 24 * 3600 * 1000) {
			new GoogleAnalytics().asyncReport("Version check " + Version.getVersion());
			checkForLatestVersion();
		}
	}

	private void checkForLatestVersion() {
		checkVersion.check(false);
		guiConfig.lastVersionCheck = System.currentTimeMillis();
	}
}
