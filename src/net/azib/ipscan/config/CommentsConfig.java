/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.core.ScanningResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Level;

/**
 * CommentsConfig - a class for encapsulating of loading/storing of comments.
 *
 * Comments are persisted to a plain file (comments.txt) inside the program's
 * config directory, so they survive program restarts and updates (unlike the
 * previous Java Preferences based implementation, which relied on the OS user
 * registry and was not always flushed to disk).
 *
 * @author Anton Keks
 */
public class CommentsConfig {
	private final File file;
	private final Properties comments = new Properties();

	public CommentsConfig(Config config) {
		this(new File(config.getConfigDir(), "comments.txt"));
	}

	public CommentsConfig(File file) {
		this.file = file;
		load();
	}

	private void load() {
		if (!file.exists()) return;
		try (var in = new FileInputStream(file)) {
			comments.load(in);
		}
		catch (IOException e) {
			LoggerFactory.getLogger().log(Level.WARNING, "Cannot load comments from " + file, e);
		}
	}

	private void save() {
		try {
			var dir = file.getParentFile();
			if (dir != null && !dir.exists()) dir.mkdirs();
			try (var out = new FileOutputStream(file)) {
				comments.store(out, "Angry IP Scanner comments");
			}
		}
		catch (IOException e) {
			LoggerFactory.getLogger().log(Level.WARNING, "Cannot save comments to " + file, e);
		}
	}

	public String getComment(InetAddress address, String mac) {
		String comment = mac != null ? comments.getProperty(mac) : null;
		if (comment == null) {
			comment = comments.getProperty(address.getHostAddress());
			// migrate old IP-based comment to MAC-based for consistency
			if (comment != null && mac != null) {
				comments.setProperty(mac, comment);
				comments.remove(address.getHostAddress());
				save();
			}
		}
		return comment;
	}

	public String getComment(ScanningResult result) {
		return getComment(result.getAddress(), result.getMac());
	}

	public void setComment(ScanningResult result, String comment) {
		var mac = result.getMac();
		var key = mac != null ? mac : result.getAddress().getHostAddress();

		// remove any stale IP-based entry to avoid duplicates when keying by MAC
		comments.remove(result.getAddress().getHostAddress());

		if (comment == null || comment.isEmpty())
			comments.remove(key);
		else
			comments.setProperty(key, comment);

		// persist immediately so that comments are not lost on crash or restart
		save();
	}
}
