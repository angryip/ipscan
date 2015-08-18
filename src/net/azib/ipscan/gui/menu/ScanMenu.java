package net.azib.ipscan.gui.menu;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.actions.ScanMenuActions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScanMenu extends AbstractMenu {

	@Inject
	public ScanMenu(Shell parent,
					ScanMenuActions.LoadFromFile loadFromFile,
					ScanMenuActions.SaveAll saveAll,
					ScanMenuActions.SaveSelection saveSelection,
					ScanMenuActions.Quit quit) {

		super(parent);

//		initMenuItem(subMenu, "menu.scan.newWindow", "Ctrl+N", new Integer(SWT.MOD1 | 'N'), initListener(FileActions.NewWindow.class));
//		initMenuItem(subMenu, null, null, null, null);
		initMenuItem(this, "menu.scan.load", "", SWT.MOD1 | 'O', loadFromFile, true);
		initMenuItem(this, "menu.scan.exportAll", "Ctrl+S", SWT.MOD1 | 'S', saveAll, false);
		initMenuItem(this, "menu.scan.exportSelection", null, null, saveSelection, false);
//		initMenuItem(subMenu, null, null, null, null);
//		initMenuItem(subMenu, "menu.scan.exportPreferences", null, null, null);
//		initMenuItem(subMenu, "menu.scan.importPreferences", null, null, null);
		if (!Platform.MAC_OS) {
			initMenuItem(this, null, null, null, null);
			initMenuItem(this, "menu.scan.quit", "Ctrl+Q", SWT.MOD1 | 'Q', quit);
		}
	}

	@Override
	public String getId() {
		return "menu.scan";
	}
}
