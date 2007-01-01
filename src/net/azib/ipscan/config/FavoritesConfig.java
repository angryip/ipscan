/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * FavoritesConfig
 *
 * @author anton
 */
public class FavoritesConfig extends NamedListConfig {

	public FavoritesConfig(Preferences preferences) {
		super(preferences, "favorites");
	}

}
