/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;

import java.util.MissingResourceException;
import java.util.prefs.Preferences;

/**
 * Convenience base class for built-in fetchers
 *
 * @author Anton Keks
 */
public abstract class AbstractFetcher implements Fetcher {

	public String getName() {
		return Labels.getLabel(getId());
	}

	public String getFullName() {
		return getName();
	}

	public String getInfo() {
		try {
			return Labels.getLabel(getId() + ".info");
		}
		catch (MissingResourceException e) {
			return null;
		}
	}

	public Preferences getPreferences() {
		return Config.getConfig().getPreferences().node(getId().replace("fetcher.", ""));
	}

	public Class<? extends FetcherPrefs> getPreferencesClass() {
		// no preferences by default
		return null;
	}

	public void init() {
		// nothing's here by default
	}

	public void cleanup() {
		// nothing's here by default
	}
}
