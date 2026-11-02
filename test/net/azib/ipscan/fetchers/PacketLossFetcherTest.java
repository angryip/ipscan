package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningResult.ResultType;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class PacketLossFetcherTest extends AbstractPingFetcherTest {
	@Override
	protected PingFetcher createFetcher() {
		return new PacketLossFetcher(pingerRegistry, config);
	}

	@Test
	public void packetLoss() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenReturn(aliveResult());

		assertEquals("0/1 (0%)", fetcher.scan(subject));
		assertEquals(ResultType.ALIVE, subject.getResultType());
	}

	@Test
	public void pingFailureIsNotReportedAsDead() throws Exception {
		when(pinger.ping(subject, config.pingCount)).thenThrow(new IOException("failure"));

		assertNull(fetcher.scan(subject));
		assertEquals(ResultType.UNKNOWN, subject.getResultType());
	}
}
