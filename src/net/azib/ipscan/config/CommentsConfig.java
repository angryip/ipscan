/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

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
	
	public String getComment(InetAddress address) {
		return preferences.get(address.getHostAddress(), null);
	}
	
	public void setComment(InetAddress address, String comment) {
		preferences.put(address.getHostAddress(), comment);
	}
}
