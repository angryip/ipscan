/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.gui.PreferencesDialog;

/**
 * PortsFetcherPrefs - just opens the appropriate tab of the PreferencesDialog
 *
 * @author Anton Keks
 */
public class PortsFetcherPrefs implements Runnable {
	
	private PreferencesDialog preferencesDialog;

	public PortsFetcherPrefs(PreferencesDialog preferencesDialog) {
		this.preferencesDialog = preferencesDialog;
	}

	public void run() {
		preferencesDialog.openTab(1);
	}
	
}
