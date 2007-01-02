/**
 * 
 */
package net.azib.ipscan.core.values;

import static org.junit.Assert.*;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.values.NotAvailableValue;

import org.junit.Test;

/**
 * NotAvailableValueTest
 *
 * @author anton
 */
public class NotAvailableValueTest {

	@Test
	public void testEquals() throws Exception {
		assertEquals(NotAvailableValue.INSTANCE, NotAvailableValue.INSTANCE);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(Labels.getLabel("fetcher.value.notAvailable"), NotAvailableValue.INSTANCE.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotAvailableValue.class));
		assertEquals(0, NotAvailableValue.INSTANCE.compareTo(NotAvailableValue.INSTANCE));
		assertEquals(-1, NotAvailableValue.INSTANCE.compareTo("Hello"));
		assertEquals(1, NotAvailableValue.INSTANCE.compareTo(null));
	}
}
