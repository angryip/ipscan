package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.di.Injector;

import java.util.prefs.Preferences;

@Module
public class ConfigModule {
	public void register(Injector i) {
		Config config = Config.getConfig();
		i.register(Config.class, config);
		i.register(Labels.class, Labels.getInstance());
		i.register(Preferences.class, config.getPreferences());
		i.register(ScannerConfig.class, config.forScanner());
		i.register(OpenersConfig.class, config.forOpeners());
		i.register(FavoritesConfig.class, config.forFavorites());
		i.register(GUIConfig.class, config.forGUI());
	}

	@Provides Config getConfig() {
		return Config.getConfig();
	}

	@Provides Labels getLabels() {
		return Labels.getInstance();
	}

	@Provides public Preferences getPreferences() {
		return getConfig().getPreferences();
	}

	@Provides public ScannerConfig forScanner() {
		return getConfig().forScanner();
	}

	@Provides public OpenersConfig forOpeners() {
		return getConfig().forOpeners();
	}

	@Provides public FavoritesConfig forFavorites() {
		return getConfig().forFavorites();
	}

	@Provides public GUIConfig forGUI() {
		return getConfig().forGUI();
	}
}
