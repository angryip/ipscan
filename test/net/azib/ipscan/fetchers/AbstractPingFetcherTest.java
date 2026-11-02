package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.net.Pinger;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.feeders.Feeder;
import org.junit.After;
import org.junit.Before;

import java.net.InetAddress;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class AbstractPingFetcherTest extends AbstractFetcherTestCase {
	protected final PingerRegistry pingerRegistry = mock(PingerRegistry.class);
	protected final Pinger pinger = mock(Pinger.class);
	protected final ScannerConfig config = mock(ScannerConfig.class);
	protected ScanningSubject subject;

	protected abstract PingFetcher createFetcher();

	@Before
	@Override
	public void setUp() throws Exception {
		config.pingCount = 3;
		fetcher = createFetcher();
		when(pingerRegistry.createPinger(anyBoolean())).thenReturn(pinger);
		fetcher.init(mock(Feeder.class));
		subject = new ScanningSubject(InetAddress.getByName("192.168.1.1"));
	}

	@After
	public void tearDown() {
		fetcher.cleanup();
	}

	protected PingResult aliveResult() {
		var result = new PingResult(subject.getAddress(), 1);
		result.addReply(1);
		return result;
	}

	protected PingResult deadResult() {
		return new PingResult(subject.getAddress(), config.pingCount);
	}
}
