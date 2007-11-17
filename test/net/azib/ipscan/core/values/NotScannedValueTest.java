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
public class NotScannedValueTest {
	
	@Test
	public void testEquals() throws Exception {
		assertEquals(NotScannedValue.INSTANCE, NotScannedValue.INSTANCE);
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals(Config.getConfig().getScanner().notScannedText, NotScannedValue.INSTANCE.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(NotScannedValue.class));
		assertEquals(0, NotScannedValue.INSTANCE.compareTo(NotScannedValue.INSTANCE));
		assertEquals(-1, NotScannedValue.INSTANCE.compareTo("Hello"));
		assertEquals(1, NotScannedValue.INSTANCE.compareTo(null));
	}

}
