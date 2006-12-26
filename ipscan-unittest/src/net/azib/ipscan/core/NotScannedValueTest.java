/**
 * 
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.Labels;
import junit.framework.TestCase;

/**
 * NotScannedValueTest
 *
 * @author anton
 */
public class NotScannedValueTest extends TestCase {
	public void testEquals() throws Exception {
		assertEquals(NotScannedValue.INSTANCE, NotScannedValue.INSTANCE);
	}
	
	public void testToString() throws Exception {
		assertEquals(Labels.getLabel("fetcher.value.notScanned"), NotScannedValue.INSTANCE.toString());
	}
	
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotScannedValue.class));
		assertEquals(0, NotScannedValue.INSTANCE.compareTo(NotScannedValue.INSTANCE));
		assertEquals(-1, NotScannedValue.INSTANCE.compareTo("Hello"));
		assertEquals(1, NotScannedValue.INSTANCE.compareTo(null));
	}

}
