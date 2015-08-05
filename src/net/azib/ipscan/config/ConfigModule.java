package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.exporters.Exporter;

import javax.inject.Named;
import java.util.List;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ConfigModule {
	@Provides
	public ComponentRegistry getRegistry(@Named("plugins") List<Class> plugins, @Named("fetchers") List<Class> fetchers) {
		return new ComponentRegistry(plugins, fetchers);
	}
}
