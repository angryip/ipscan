package net.azib.ipscan.core.net;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.ScannerConfig;

/**
 * @author Andriy Kryvtsun
 */
@Module
public class PingerRegistryModule {

	@Provides
	public PingerRegistry providePingerRegistry(ScannerConfig scannerConfig) {
		return new PingerRegistry(scannerConfig);
	}
}
