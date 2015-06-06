package net.azib.ipscan.config;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.exporters.Exporter;

import java.util.List;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ConfigModule {
	@Provides
	public ComponentRegistry getRegistry(Class<Exporter>[] exporters, List<Class> plugins) {
		return new ComponentRegistry(exporters, plugins);
	}
}
