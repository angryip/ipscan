/**
 * 
 */
package net.azib.ipscan.core.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.azib.ipscan.config.Config;

import org.junit.Test;

/**
 * NotAvailableValueTest
 *
 * @author Anton Keks
 */
public class NotAvailableValueTest {

	@Test
	public void testEquals() throws Exception {
		assertEquals(NotAvailableValue.INSTANCE, NotAvailableValue.INSTANCE);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(Config.getConfig().forScanner().notAvailableText, NotAvailableValue.INSTANCE.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotAvailableValue.class));
		assertEquals(0, NotAvailableValue.INSTANCE.compareTo(NotAvailableValue.INSTANCE));
		assertEquals(-1, NotAvailableValue.INSTANCE.compareTo("Hello"));
		assertEquals(1, NotAvailableValue.INSTANCE.compareTo(null));
	}
}
