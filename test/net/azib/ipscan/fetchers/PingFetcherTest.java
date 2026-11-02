package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningResult.ResultType;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * PingFetcherTest
 *
 * @author Anton Keks
 */
public class PingFetcherTest extends AbstractPingFetcherTest {
	@Override
	protected PingFetcher createFetcher() {
		return new PingFetcher(pingerRegistry, config);
	}

	@Test
	public void aliveHost() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenReturn(aliveResult());

		assertNotNull(fetcher.scan(subject));
		assertEquals(ResultType.ALIVE, subject.getResultType());
		assertFalse(subject.isAddressAborted());
	}

	@Test
	public void deadHost() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenReturn(deadResult());

		assertNull(fetcher.scan(subject));
		assertEquals(ResultType.DEAD, subject.getResultType());
		assertTrue(subject.isAddressAborted());
	}

	@Test
	public void pingFailureIsNotReportedAsDead() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenThrow(new IOException("failure"));

		assertNull(fetcher.scan(subject));
		assertEquals(ResultType.UNKNOWN, subject.getResultType());
		assertFalse(subject.isAddressAborted());
	}
}
