/**
 * 
 */
package net.azib.ipscan.core.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.azib.ipscan.config.Config;

import org.junit.Test;

/**
 * NotScannedValueTest
 *
 * @author Anton Keks
 */
public class NotScannedTest {
	
	@Test
	public void testEquals() throws Exception {
		assertEquals(NotScanned.VALUE, NotScanned.VALUE);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(Config.getConfig().forScanner().notScannedText, NotScanned.VALUE.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotScanned.class));
		assertEquals(0, NotScanned.VALUE.compareTo(NotScanned.VALUE));
		Empty.setSortDirection(true);
		assertEquals(1, NotScanned.VALUE.compareTo("Hello"));
		assertEquals(1, NotScanned.VALUE.compareTo(null));
		Empty.setSortDirection(false);
		assertEquals(-1, NotScanned.VALUE.compareTo("Hello"));
		Empty.setSortDirection(true);
	}

}
