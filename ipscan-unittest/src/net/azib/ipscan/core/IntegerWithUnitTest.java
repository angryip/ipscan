/**
 * 
 */
package net.azib.ipscan.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.values.IntegerWithUnit;

import org.junit.Test;

/**
 * IntegerWithUnitTest
 *
 * @author anton
 */
public class IntegerWithUnitTest {
	
	@Test
	public void testIntValue() throws Exception {
		assertEquals(0, new IntegerWithUnit(0, "a").intValue());
		assertEquals(-1, new IntegerWithUnit(-1, "a").intValue());
		assertEquals(Integer.MAX_VALUE, new IntegerWithUnit(Integer.MAX_VALUE, "a").intValue());
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("151" + Labels.getLabel("fetcher.value.ms"), new IntegerWithUnit(151, "fetcher.value.ms").toString());
	}

	@Test
	public void testEquals() throws Exception {
		assertTrue(new IntegerWithUnit(666, null).equals(new IntegerWithUnit(666, null)));
		assertTrue(new IntegerWithUnit(42, "a").equals(new IntegerWithUnit(42, "b")));
		assertFalse(new IntegerWithUnit(0, null).equals(null));
		assertFalse(new IntegerWithUnit(42, "a").equals(new IntegerWithUnit(43, "a")));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(3, new IntegerWithUnit(3, null).hashCode());
		assertEquals(-31, new IntegerWithUnit(-31, null).hashCode());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		assertTrue(Comparable.class.isAssignableFrom(IntegerWithUnit.class));
		assertEquals(0, new IntegerWithUnit(1, null).compareTo(new IntegerWithUnit(1, null)));
		assertEquals(1, new IntegerWithUnit(123456789, null).compareTo(new IntegerWithUnit(123456, null)));
		assertEquals(-1, new IntegerWithUnit(12, null).compareTo(new IntegerWithUnit(123456, null)));
		assertEquals(1, new IntegerWithUnit(12, null).compareTo(null));
		IntegerWithUnit instance = new IntegerWithUnit(211082, null);
		assertEquals(0, instance.compareTo(instance));
	}
}
