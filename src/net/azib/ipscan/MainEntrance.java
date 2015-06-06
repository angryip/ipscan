package net.azib.ipscan;

import dagger.Component;
import net.azib.ipscan.config.ComponentRegistry;
import net.azib.ipscan.config.ConfigModule;
import net.azib.ipscan.core.PluginsModule;

/**
 * Created by englishman on 6/6/15.
 */
@Component(modules = {ConfigModule.class, PluginsModule.class})
public interface MainEntrance {
	ComponentRegistry getRegistry();
}
