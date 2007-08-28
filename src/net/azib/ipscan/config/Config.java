/**
 * 
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

/**
 * This class encapsulates preferences of the program.
 * It is a singleton class.
 * 
 * @author Anton Keks
 */
public final class Config {
	
	private static Preferences preferences;
	
	/** easily accessible global configuration */
	private static GlobalConfig globalConfig;
	/** favorites are stored here */
	private static NamedListConfig favoritesConfig;
	/** openers are stored here */
	private static OpenersConfig openersConfig;
	/** various dimensions are stored here */
	private static DimensionsConfig dimensionsConfig;
	
	private Config() {
	}
	
	/**
	 * Initializes the singleton instance
	 */
	public static void initialize() {
		preferences = Preferences.userRoot().node("ipscan");
		globalConfig = new GlobalConfig(preferences);
		favoritesConfig = new FavoritesConfig(preferences);
		openersConfig = new OpenersConfig(preferences);
		dimensionsConfig = new DimensionsConfig(preferences);
	}

	public static void store() {
		globalConfig.store();
		favoritesConfig.store();
		openersConfig.store();
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
	public static NamedListConfig getFavoritesConfig() {
		return favoritesConfig;
	}

	/**
	 * @return Openers config (quick access);
	 */
	public static OpenersConfig getOpenersConfig() {
		return openersConfig;
	}
	
	/**
	 * @return Dimensions config (quick access);
	 */
	public static DimensionsConfig getDimensionsConfig() {
		return dimensionsConfig;
	}
	
}
