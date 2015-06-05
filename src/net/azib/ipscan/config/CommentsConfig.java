/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.MACFetcher;

import java.net.InetAddress;
import java.util.prefs.Preferences;

/**
 * CommentsConfig - a class for encapsulating of loading/storing of comments.
 *
 * @author Anton Keks
 */
public class CommentsConfig {
	private Preferences preferences;

	public CommentsConfig(Preferences preferences) {
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
		return getComment(result.getAddress(), getMac(macIndex, result));
	}

	private String getMac(int macIndex, ScanningResult result) {
		if (macIndex < 0) return null;
		Object macValue = result.getValues().get(macIndex);
		return macValue instanceof String ? (String) macValue : null;
	}

	public void setComment(ScanningResultList results, int resultIndex, String comment) {
		ScanningResult result = results.getResult(resultIndex);
		int macIndex = results.getFetcherIndex(MACFetcher.ID);

		String key = result.getAddress().getHostAddress();

		if (macIndex >= 0) {
			// remove ip-based comment if we set a mac-based one
			preferences.remove(key);
			String mac = getMac(macIndex, result);
			if (mac != null) key = mac;
		}

		if (comment == null || comment.length() == 0)
			preferences.remove(key);
		else
			preferences.put(key, comment);
	}
}
