package net.azib.ipscan.core.values;

import net.azib.ipscan.config.Config;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * NotAvailableValueTest
 *
 * @author Anton Keks
 */
public class NotAvailableTest {

	@Test
	public void testEquals() throws Exception {
		assertEquals(NotAvailable.VALUE, NotAvailable.VALUE);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(Config.getConfig().forScanner().notAvailableText, NotAvailable.VALUE.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotAvailable.class));
		assertEquals(0, NotAvailable.VALUE.compareTo(NotAvailable.VALUE));
		Empty.setSortDirection(true);
		assertEquals(1, NotAvailable.VALUE.compareTo("Hello"));
		assertEquals(1, NotAvailable.VALUE.compareTo(null));
		Empty.setSortDirection(false);
		assertEquals(-1, NotAvailable.VALUE.compareTo("Hello"));
		Empty.setSortDirection(true);
	}
}
