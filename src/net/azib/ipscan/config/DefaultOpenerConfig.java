/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * DefaultOpenerConfig - stores the per-IP default Opener assignment.
 *
 * Persisted to a plain file (openers-defaults.txt) inside the program's config
 * directory, so the assignments survive program restarts and updates.
 *
 * @author Anton Keks
 */
public class DefaultOpenerConfig {
	private final File file;
	private final Properties defaults = new Properties();

	public DefaultOpenerConfig(Config config) {
		this(new File(config.getConfigDir(), "openers-defaults.txt"));
	}

	public DefaultOpenerConfig(File file) {
		this.file = file;
		load();
	}

	private void load() {
		if (!file.exists()) return;
		try (var in = new FileInputStream(file)) {
			defaults.load(in);
		}
		catch (IOException e) {
			LoggerFactory.getLogger().log(Level.WARNING, "Cannot load default openers from " + file, e);
		}
	}

	private void save() {
		try {
			var dir = file.getParentFile();
			if (dir != null && !dir.exists()) dir.mkdirs();
			try (var out = new FileOutputStream(file)) {
				defaults.store(out, "Angry IP Scanner default openers per IP");
			}
		}
		catch (IOException e) {
			LoggerFactory.getLogger().log(Level.WARNING, "Cannot save default openers to " + file, e);
		}
	}

	public String get(String ip) {
		return defaults.getProperty(ip);
	}

	public void set(String ip, String openerName) {
		if (openerName == null) defaults.remove(ip);
		else defaults.setProperty(ip, openerName);
		save();
	}
}
