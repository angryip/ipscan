/**
 * 
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.Labels;
import junit.framework.TestCase;

/**
 * NotAvailableValueTest
 *
 * @author anton
 */
public class NotAvailableValueTest extends TestCase {

	public void testEquals() throws Exception {
		assertEquals(NotAvailableValue.INSTANCE, NotAvailableValue.INSTANCE);
	}
	
	public void testToString() throws Exception {
		assertEquals(Labels.getLabel("fetcher.value.notAvailable"), NotAvailableValue.INSTANCE.toString());
	}
	
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotAvailableValue.class));
		assertEquals(0, NotAvailableValue.INSTANCE.compareTo(NotAvailableValue.INSTANCE));
		assertEquals(-1, NotAvailableValue.INSTANCE.compareTo("Hello"));
		assertEquals(1, NotAvailableValue.INSTANCE.compareTo(null));
	}
}
