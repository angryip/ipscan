package net.azib.ipscan.fetchers;

import dagger.Module;
import dagger.Provides;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.exporters.*;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Kryvtsun
 */
@Module
public class FetcherModule {

	@Provides @Named("fetchers")
	public List<Class> provideClasses() {

		List<Class> classes = new ArrayList<Class>();

		classes.add(IPFetcher.class);
		classes.add(PingFetcher.class);
		classes.add(PingTTLFetcher.class);
		classes.add(HostnameFetcher.class);
		classes.add(PortsFetcher.class);
		classes.add(FilteredPortsFetcher.class);
		classes.add(WebDetectFetcher.class);
		classes.add(HTTPSenderFetcher.class);
		classes.add(CommentFetcher.class);
		classes.add(NetBIOSInfoFetcher.class);
		classes.add(Platform.WINDOWS ? WinMACFetcher.class : UnixMACFetcher.class);
		classes.add(MACVendorFetcher.class);

		return classes;
	}
}
