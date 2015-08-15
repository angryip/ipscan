package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;

import java.util.prefs.Preferences;

@Module
public class ConfigModule {
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

	@Provides public GUIConfig forGUI() {
		return getConfig().forGUI();
	}
}
