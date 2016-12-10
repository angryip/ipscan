package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.actions.HelpMenuActions;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;

public class Startup {
	@Inject Shell shell;
	@Inject GUIConfig guiConfig;
	@Inject HelpMenuActions.CheckVersion checkVersion;

	@Inject public Startup() {}

	public void onStart() {
		if (guiConfig.isFirstRun) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					GettingStartedDialog dialog = new GettingStartedDialog();
					if (Platform.CRIPPLED_WINDOWS)
						dialog.prependText(Labels.getLabel("text.crippledWindowsInfo"));
					if (Platform.GNU_JAVA)
						dialog.prependText(Labels.getLabel("text.gnuJavaInfo"));

					shell.forceActive();
					dialog.open();
					guiConfig.isFirstRun = false;
				}
			});
		}
		else if (!Version.getVersion().equals(guiConfig.lastRunVersion)) {
			guiConfig.lastRunVersion = Version.getVersion();
		}
		else if (System.currentTimeMillis() - guiConfig.lastVersionCheck > 30L * 24 * 3600 * 1000) {
			checkVersion.check(false);
			guiConfig.lastVersionCheck = System.currentTimeMillis();
		}
	}
}
