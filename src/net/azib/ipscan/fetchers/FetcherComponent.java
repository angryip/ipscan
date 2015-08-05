package net.azib.ipscan.fetchers;

import dagger.Component;
import net.azib.ipscan.config.ConfigModule;
import net.azib.ipscan.core.net.PingerRegistryModule;

/**
 * @author Andriy Kryvtsun
 */
@Component(modules = {FetcherModule.class, ConfigModule.class, PingerRegistryModule.class})
public interface FetcherComponent {
	FetcherRegistry get();
}
