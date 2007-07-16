/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * FavoritesConfig
 *
 * @author Anton Keks
 */
public class FavoritesConfig extends NamedListConfig {

	public FavoritesConfig(Preferences preferences) {
		super(preferences, "favorites");
	}

}
