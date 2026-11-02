package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningResult.ResultType;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * PingTTLFetcherTest
 *
 * @author Anton Keks
 */
public class PingTTLFetcherTest extends AbstractPingFetcherTest {
	@Override
	protected PingFetcher createFetcher() {
		return new PingTTLFetcher(pingerRegistry, config);
	}

	@Test
	public void aliveHostTTL() throws Exception {
		var result = aliveResult();
		result.setTTL(64);
		when(pinger.ping(subject, config.pingCount)).thenReturn(result);

		assertEquals(64, fetcher.scan(subject));
		assertEquals(ResultType.ALIVE, subject.getResultType());
	}

	@Test
	public void pingFailureIsNotReportedAsDead() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenThrow(new IOException("failure"));

		assertNull(fetcher.scan(subject));
		assertEquals(ResultType.UNKNOWN, subject.getResultType());
	}
}
