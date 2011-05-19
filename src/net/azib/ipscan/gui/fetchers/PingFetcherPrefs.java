/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;
import net.azib.ipscan.gui.PreferencesDialog;

/**
 * PingFetcherPrefs - just opens the appropriate tab of the PreferencesDialog
 *
 * @author Anton Keks
 */
public class PingFetcherPrefs implements FetcherPrefs {
	
	private PreferencesDialog preferencesDialog;

	public PingFetcherPrefs(PreferencesDialog preferencesDialog) {
		this.preferencesDialog = preferencesDialog;
	}

	public void openFor(Fetcher fetcher) {
		preferencesDialog.openTab(0);
	}
	
}
