package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentFetcherTest {
	private CommentsConfig commentsConfig = mock(CommentsConfig.class);
	private MACFetcher macFetcher = new MACFetcher() {
		@Override protected String resolveMAC(ScanningSubject subject) {
			return "00:01:02:03:04:05";
		}
	};
	private CommentFetcher fetcher = new CommentFetcher(commentsConfig, macFetcher);

	@Test
	public void commentIsFoundByMACEvenIfMACFetcherRunsLater() {
		var subject = new ScanningSubject(InetAddress.getLoopbackAddress());
		when(commentsConfig.getComment(subject.getAddress(), "00:01:02:03:04:05")).thenReturn("My router");
		assertEquals("My router", fetcher.scan(subject));
	}
}
