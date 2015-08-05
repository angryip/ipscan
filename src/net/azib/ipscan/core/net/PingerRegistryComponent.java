package net.azib.ipscan.core.net;

import dagger.Component;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.ConfigModule;
import net.azib.ipscan.fetchers.FetcherModule;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * @author Andriy Kryvtsun
 */
@Component(modules = {PingerRegistryModule.class, ConfigModule.class})
public interface PingerRegistryComponent {
	PingerRegistry get();
}
