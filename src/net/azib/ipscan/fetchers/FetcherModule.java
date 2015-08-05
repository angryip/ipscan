package net.azib.ipscan.fetchers;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.exporters.*;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author Andriy Kryvtsun
 */
@Module
public class FetcherModule {

	@Provides
	public Fetcher[] provideFetchers(PingerRegistry pingerRegistry, ScannerConfig scannerConfig, CommentsConfig commentsConfig) {

		MACFetcher macFetcher = Platform.WINDOWS ? new WinMACFetcher() : new UnixMACFetcher();

		return new Fetcher[] {
			new IPFetcher(),
			new PingFetcher(pingerRegistry, scannerConfig),
			new PingTTLFetcher(pingerRegistry, scannerConfig),
			new HostnameFetcher(),
			new PortsFetcher(scannerConfig),
			new FilteredPortsFetcher(scannerConfig),
			new WebDetectFetcher(scannerConfig),
			new HTTPSenderFetcher(scannerConfig),
			new CommentFetcher(commentsConfig),
			new NetBIOSInfoFetcher(),
				macFetcher,
			new MACVendorFetcher(macFetcher),
		};
	}

	@Provides
	public FetcherRegistry provideFetcherRegistry(Fetcher[] fetchers, Preferences preferences) {
		return new FetcherRegistry(fetchers, preferences);
	}
}
