/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResult;

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

	public String getComment(ScanningResult result) {
		return getComment(result.getAddress(), result.getMac());
	}

	public void setComment(ScanningResult result, String comment) {
		String key = result.getAddress().getHostAddress();

		if (result.getMac() != null) {
			// remove ip-based comment if we set a mac-based one
			preferences.remove(key);
			String mac = result.getMac();
			if (mac != null) key = mac;
		}

		if (comment == null || comment.isEmpty())
			preferences.remove(key);
		else
			preferences.put(key, comment);
	}
}
