package net.azib.ipscan.gui.menu;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.actions.HelpMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Andriy Kryvtsun
 */
@Module
public class HelpMenu {

	@Provides @Named("menu.help") @Singleton
	public Menu providesHelpMenu(@Named("mainMenu") Menu mainMenu,
								 HelpMenuActions.GettingStarted gettingStarted,
								 HelpMenuActions.Website website,
								 HelpMenuActions.FAQ faq,
								 HelpMenuActions.Plugins plugins,
								 HelpMenuActions.CommandLineUsage commandLineUsage,
								 HelpMenuActions.CheckVersion checkVersion,
								 HelpMenuActions.About about) {

		Menu subMenu = new Menu(mainMenu.getShell(), SWT.DROP_DOWN);

		initMenuItem(subMenu, "menu.help.gettingStarted", !Platform.MAC_OS ? "F1" : null, Platform.MAC_OS ? SWT.HELP : SWT.F1, gettingStarted);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.website", null, null, website);
		initMenuItem(subMenu, "menu.help.faq", null, null, faq);
		initMenuItem(subMenu, "menu.help.plugins", null, null, plugins);
		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(subMenu, "menu.help.cmdLine", null, null, commandLineUsage);

		if (!Platform.MAC_OS) {
			// mac will have these in the 'application' menu
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.checkVersion", null, null, checkVersion);
			initMenuItem(subMenu, null, null, null, null);
			initMenuItem(subMenu, "menu.help.about", null, null, about);
		}

		return subMenu;
	}

	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener) {
		return initMenuItem(parent, label, acceleratorText, accelerator, listener, false);
	}

	static MenuItem initMenuItem(Menu parent, String label, String acceleratorText, Integer accelerator, Listener listener, boolean disableDuringScanning) {
		MenuItem menuItem = new MenuItem(parent, label == null ? SWT.SEPARATOR : SWT.PUSH);

		if (label != null)
			menuItem.setText(Labels.getLabel(label) + (acceleratorText != null ? "\t" + acceleratorText : ""));

		if (accelerator != null)
			menuItem.setAccelerator(accelerator);

		if (listener != null)
			menuItem.addListener(SWT.Selection, listener);
		else
			menuItem.setEnabled(false);

		if (disableDuringScanning) {
			menuItem.setData("disableDuringScanning", true);
		}

		return menuItem;
	}
}
