package net.azib.ipscan.config;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * This class encapsulates preferences of the program.
 * It is a singleton class.
 * 
 * @author Anton Keks
 */
public final class Config {
	private Preferences preferences;
	public String language;
	public String gaClientId;
	public boolean allowReports;

	/** easily accessible scanner configuration */
	private ScannerConfig scannerConfig;
	/** various GUI preferences and dimensions are stored here */
	private GUIConfig guiConfig;
	/** favorites are stored here */
	private FavoritesConfig favoritesConfig;
	/** openers are stored here */
	private OpenersConfig openersConfig;
	/** default openers per IP are stored here */
	private DefaultOpenerConfig defaultOpenerConfig;

	Config() {
		preferences = Preferences.userRoot().node("ipscan");
		scannerConfig = new ScannerConfig(preferences);
		guiConfig = new GUIConfig(preferences);
		favoritesConfig = new FavoritesConfig(preferences);
		openersConfig = new OpenersConfig(preferences);
		defaultOpenerConfig = new DefaultOpenerConfig(this);
		language = preferences.get("language", "system");
		gaClientId = preferences.get("gaClientId", null);
		if (gaClientId == null) {
			var random = new SecureRandom();
			var firstPart = 1000000000L + (long)(random.nextDouble() * 9000000000L);
			var secondPart = 1000000000L + (long)(random.nextDouble() * 9000000000L);
			gaClientId = firstPart + "." + secondPart;
			preferences.put("gaClientId", gaClientId);
		}
		allowReports = preferences.getBoolean("allowReports", true);
	}

	private static class ConfigHolder {
		static final Config INSTANCE = new Config();
	}

	public static Config getConfig() {
		return ConfigHolder.INSTANCE;
	}

	public void store() {
		preferences.put("language", language);
		preferences.putBoolean("allowReports", allowReports);
		scannerConfig.store();
		guiConfig.store();
		favoritesConfig.store();
		openersConfig.store();
	}

	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * @return directory where user data (e.g. comments) is stored persistently.
	 *         Prefers a "config" directory next to the running jar/exe (portable mode),
	 *         falling back to the user's home directory if it is not writable.
	 *         This survives program updates and restarts.
	 */
	public File getConfigDir() {
		try {
			var codeSource = Config.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			var baseDir = new File(codeSource);
			if (baseDir.isFile()) baseDir = baseDir.getParentFile();
			var portableDir = new File(baseDir, "config");
			if (!portableDir.exists()) portableDir.mkdirs();
			if (portableDir.canWrite() && isWritable(portableDir)) return portableDir;
		}
		catch (Exception ignore) {
		}
		return new File(System.getProperty("user.home"), ".ipscan");
	}

	/**
	 * On Windows, {@link java.io.File#canWrite()} checks only the read-only
	 * attribute flag, not the actual ACL / security descriptor. Directories
	 * such as <code>C:\Program Files</code> are therefore reported as writable
	 * even though the user cannot create files there without elevation.
	 * We must actually attempt to create a file to know for sure.
	 */
	private static boolean isWritable(File dir) {
		try {
			var test = File.createTempFile("ips", null, dir);
			test.delete();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	/** 
	 * @return ScannerConfig instance (quick access)
	 */
	public ScannerConfig forScanner() {
		return scannerConfig;
	}
	
	/**
	 * @return Favorites config
	 */
	public FavoritesConfig forFavorites() {
		return favoritesConfig;
	}

	/**
	 * @return Openers config (only local access);
	 */
	public OpenersConfig forOpeners() {
		return openersConfig;
	}

	public DefaultOpenerConfig forDefaultOpeners() {
		return defaultOpenerConfig;
	}
	
	/**
	 * @return Dimensions config (quick access);
	 */
	public GUIConfig forGUI() {
		return guiConfig;
	}

	public Locale getLocale() {
		if (language == null || "system".equals(language)) {
			return System.getProperty("locale") == null ? Locale.getDefault() : createLocale(System.getProperty("locale"));
		}
		else {
			return createLocale(language);
		}
	}

	private Locale createLocale(String locale) {
		return Locale.forLanguageTag(locale.replace('_', '-'));
	}

	public String getGaClientId() {
		return gaClientId;
	}
}
