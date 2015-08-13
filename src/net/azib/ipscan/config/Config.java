/**
 * 
 */
package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;

import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * This class encapsulates preferences of the program.
 * It is a singleton class.
 * 
 * @author Anton Keks
 */
@Module
public final class Config {
	
	/** Singleton instance */
	private static Config globalConfig;
	
	private Preferences preferences;
	public String language;

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
		language = preferences.get("language", "system");
	}
	
	/**
	 * Initializes the singleton instance
	 */
	@Provides public static Config getConfig() {
		if (globalConfig == null) {
			globalConfig = new Config();
		}
		return globalConfig;
	}

	public void store() {
		preferences.put("language", language);
		scannerConfig.store();
		guiConfig.store();
		favoritesConfig.store();
		openersConfig.store();
	}

	@Provides public Preferences getPreferences() {
		return preferences;
	}

	/** 
	 * @return ScannerConfig instance (quick access)
	 */
	@Provides public ScannerConfig forScanner() {
		return scannerConfig;
	}
	
	/**
	 * @return Favorites config (only local access)
	 */
	@Provides NamedListConfig forFavorites() {
		return favoritesConfig;
	}

	/**
	 * @return Openers config (only local access);
	 */
	@Provides public OpenersConfig forOpeners() {
		return openersConfig;
	}
	
	/**
	 * @return Dimensions config (quick access);
	 */
	@Provides public GUIConfig forGUI() {
		return guiConfig;
	}

	public Locale getLocale() {
		if (language == null || "system".equals(language)) {
			return System.getProperty("locale") == null ? Locale.getDefault() : new Locale(System.getProperty("locale"));
		}
		else {
			return new Locale(language);
		}
	}
}
