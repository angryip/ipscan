package net.azib.ipscan.config;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VersionTest {

	@Test
	public void testCompareTo() {
		assertEquals(0, Version.compareTo("3.10.0", "3.10.0"));
		assertEquals(0, Version.compareTo("3.10", "3.10.0")); // parts omitted are 0
		assertTrue(Version.compareTo("3.10.1", "3.10.0") > 0);
		assertTrue(Version.compareTo("3.10.0", "3.10.1") < 0);
		assertTrue(Version.compareTo("3.11.0", "3.10.1") > 0);
		assertTrue(Version.compareTo("4.0.0", "3.10.1") > 0);
		assertTrue(Version.compareTo("3.9.9", "3.10.0") < 0);
		assertTrue(Version.compareTo("3.10.1", "3.10.1-beta") == 0); // Both parse as 3.10.1 and 3.10.1.0 (beta is 0)
		assertTrue(Version.compareTo("3.10.1", "3.10.1-1") < 0);
	}
}
