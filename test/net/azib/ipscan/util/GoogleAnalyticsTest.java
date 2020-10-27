package net.azib.ipscan.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GoogleAnalyticsTest {
	@Test
	public void extractFirstStackFrame() {
		assertEquals("java.lang.RuntimeException: Kaboom\n" +
				"net.azib.ipscan.util.GoogleAnalyticsTest.extractFirstStackFrame:10",
				GoogleAnalytics.extractFirstStackFrame(new RuntimeException("Kaboom")));
	}
}