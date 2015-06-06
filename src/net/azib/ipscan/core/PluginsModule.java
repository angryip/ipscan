package net.azib.ipscan.core;

import dagger.Module;
import dagger.Provides;

import java.util.List;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class PluginsModule {
	@Provides
	public List<Class> providePlugins() {
		return new PluginLoader().getClasses();
	}
}
