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
	
	/** Singleton instance */
	private static Config globalConfig;
	
	private Preferences preferences;
	
	/** easily accessible scanner configuration */
	private ScannerConfig scannerConfig;
	/** various GUI preferences and dimensions are stored here */
	private GUIConfig guiConfig;
	/** favorites are stored here */
	private NamedListConfig favoritesConfig;
	/** openers are stored here */
	private OpenersConfig openersConfig;
	
	private Config() {
		preferences = Preferences.userRoot().node("ipscan");
		scannerConfig = new ScannerConfig(preferences);
		guiConfig = new GUIConfig(preferences);
		favoritesConfig = new FavoritesConfig(preferences);
		openersConfig = new OpenersConfig(preferences);
	}
	
	/**
	 * Initializes the singleton instance
	 */
	public static Config getConfig() {
		if (globalConfig == null) {
			globalConfig = new Config();
		}
		return globalConfig;
	}

	public void store() {
		scannerConfig.store();
		guiConfig.store();
		favoritesConfig.store();
		openersConfig.store();
	}

	public Preferences getPreferences() {
		return preferences;
	}

	/** 
	 * @return GlobalConfig instance (quick access)
	 */
	public ScannerConfig getScanner() {
		return scannerConfig;
	}
	
	/**
	 * @return Favorites config (only local access)
	 */
	NamedListConfig getFavorites() {
		return favoritesConfig;
	}

	/**
	 * @return Openers config (only local access);
	 */
	OpenersConfig getOpeners() {
		return openersConfig;
	}
	
	/**
	 * @return Dimensions config (quick access);
	 */
	public GUIConfig getGUI() {
		return guiConfig;
	}
	
}
