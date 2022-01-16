package net.azib.ipscan.config;

import net.azib.ipscan.di.Injector;

import java.util.prefs.Preferences;

public class ConfigModule {
	public void register(Injector i) {
		Config config = Config.getConfig();
		i.register(Config.class, config);
		i.register(Labels.class, Labels.getInstance());
		i.register(Preferences.class, config.getPreferences());
		i.register(ScannerConfig.class, config.forScanner());
		i.register(OpenersConfig.class, config.forOpeners());
		i.register(FavoritesConfig.class, config.forFavorites());
	}
}
