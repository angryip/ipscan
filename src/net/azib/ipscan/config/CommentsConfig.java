/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.MACFetcher;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.prefs.Preferences;

/**
 * CommentsConfig - a class for encapsulating of loading/storing of comments.
 *
 * @author Anton Keks
 */
public class CommentsConfig {
	private Preferences preferences;

	@Inject public CommentsConfig(Preferences preferences) {
		// use a separate node for comments - they can get large
		this.preferences = preferences.node("comments");
	}
	
	public String getComment(InetAddress address, String mac) {
		String comment = null;
		if (mac != null) comment = preferences.get(mac, null);
		if (comment == null) comment = preferences.get(address.getHostAddress(), null);
		return comment;
	}

	public String getComment(ScanningResultList results, int resultIndex) {
		ScanningResult result = results.getResult(resultIndex);
		int macIndex = results.getFetcherIndex(MACFetcher.ID);
		String mac = macIndex >= 0 ? (String) result.getValues().get(macIndex) : null;
		return getComment(result.getAddress(), mac);
	}

	public void setComment(ScanningResultList results, int resultIndex, String comment) {
		ScanningResult result = results.getResult(resultIndex);
		int macIndex = results.getFetcherIndex(MACFetcher.ID);

		String key = result.getAddress().getHostAddress();

		if (macIndex >= 0) {
			// remove ip-based comment if we set a mac-based one
			preferences.remove(key);
			String mac = (String) result.getValues().get(macIndex);
			if (mac != null) key = mac;
		}

		if (comment == null || comment.length() == 0)
			preferences.remove(key);
		else
			preferences.put(key, comment);
	}
}
