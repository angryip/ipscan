package net.azib.ipscan;

import dagger.Component;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.config.ConfigModule;
import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.exporters.ExporterModule;

/**
 * Created by englishman on 6/6/15.
 */
@Component(modules = {
	ConfigModule.class,
	PluginLoader.class,
	ExporterModule.class
})
public interface MainComponent {
	ComponentRegistry get();
}
