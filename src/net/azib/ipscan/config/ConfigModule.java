package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.exporters.Exporter;

import javax.inject.Named;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ConfigModule {

	@Provides
	public Preferences providePreferences() {
		return Config.getConfig().getPreferences();
	}

	@Provides
	public ScannerConfig provideScannerConfig() {
		return Config.getConfig().forScanner();
	}

	@Provides
	public CommentsConfig provideCommentsConfig(Preferences preferences) {
		return new CommentsConfig(preferences);
	}

	@Provides
	public ComponentRegistry getRegistry(List<Class> plugins) {
		return new ComponentRegistry(plugins);
	}
}
