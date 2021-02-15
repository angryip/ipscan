package net.azib.ipscan.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GoogleAnalyticsTest {
	@Test
	public void extractFirstStackFrame() {
		assertEquals("java.lang.RuntimeException: Kaboom\n" +
				"net.azib.ipscan.util.GoogleAnalyticsTest.extractFirstStackFrame:10",
				GoogleAnalytics.extractFirstStackFrame(new RuntimeException("Kaboom")));
	}

	@Test
	public void extractFirstStackFrameWithCause() {
		assertEquals("java.lang.IllegalArgumentException: Kaboom\n" +
					"net.azib.ipscan.util.GoogleAnalyticsTest.extractFirstStackFrameWithCause:19;\n" +
					"java.io.IOException: The real stuff\n" +
					"net.azib.ipscan.util.GoogleAnalyticsTest.extractFirstStackFrameWithCause:19",
				GoogleAnalytics.extractFirstStackFrame(new IllegalArgumentException("Kaboom", new IOException("The real stuff"))));
	}
}
