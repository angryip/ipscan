/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * This class encapsulates configuration options of the program.
 * It is a singleton class.
 * 
 * @author anton
 */
public final class Config {
	
	private static Preferences preferences;
	
	/** easily accessible global configuration */
	private static GlobalConfig globalConfig;
	/** favorites are stored here */
	private static FavoritesConfig favoritesConfig;
	/** various dimensions are stored here */
	private static DimensionsConfig dimensionsConfig;
	
	private Config() {
	}
	
	/**
	 * Initializes the singleton instance
	 */
	public static void initialize() {
		preferences = Preferences.userRoot().node("ipscan");
		globalConfig = new GlobalConfig();
		favoritesConfig = new FavoritesConfig();
		dimensionsConfig = new DimensionsConfig();
	}
	
	public static void store() {
		globalConfig.store();
		favoritesConfig.store();
		dimensionsConfig.store();
	}

	public static Preferences getPreferences() {
		return preferences;
	}

	/** 
	 * @return GlobalConfig instance (quick access)
	 */
	public static GlobalConfig getGlobal() {
		return globalConfig;
	}
	
	/**
	 * @return Favorites config (quick access);
	 */
	public static FavoritesConfig getFavoritesConfig() {
		return favoritesConfig;
	}
	
	public static DimensionsConfig getDimensionsConfig() {
		return dimensionsConfig;
	}
	
}
