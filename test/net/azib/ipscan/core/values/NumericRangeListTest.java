/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.values;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;

/**
 * @author Anton Keks
 */
public class NumericRangeListTest {

	@Test
	public void testToString() {
		assertEquals("", new NumericRangeList(Collections.<Integer>emptyList(), true).toString());
		assertEquals("1", new NumericRangeList(Arrays.asList(1), true).toString());
		assertEquals("1,2", new NumericRangeList(Arrays.asList(1, 2), true).toString());
		assertEquals("1-3", new NumericRangeList(Arrays.asList(1, 2, 3), true).toString());
		assertEquals("1-3", new NumericRangeList(new TreeSet<Integer>(Arrays.asList(2, 3, 1)), true).toString());
		assertEquals("1,2,3", new NumericRangeList(Arrays.asList(1, 2, 3), false).toString());
		assertEquals("1,5,6,15", new NumericRangeList(Arrays.asList(1, 5, 6, 15), true).toString());
		assertEquals("1,5-8,15", new NumericRangeList(Arrays.asList(1, 5, 6, 7, 8, 15), true).toString());
		assertEquals("103,85,89,1", new NumericRangeList(Arrays.asList(103, 85, 89, 1), true).toString());
	}
	
	@Test
	public void testCompateTo() throws Exception {
		assertTrue(new NumericRangeList(Arrays.asList(22), false).compareTo(new NumericRangeList(Arrays.asList(80), false)) < 0);
		assertTrue(new NumericRangeList(Arrays.asList(80), false).compareTo(new NumericRangeList(Arrays.asList(22), false)) > 0);
		assertTrue(new NumericRangeList(Arrays.asList(255), false).compareTo(new NumericRangeList(Arrays.asList(255), false)) == 0);
		assertTrue(new NumericRangeList(Arrays.asList(1, 2), false).compareTo(new NumericRangeList(Arrays.asList(8080), false)) > 0);
		assertTrue(new NumericRangeList(Arrays.asList(22, 25, 27, 28), false).compareTo(new NumericRangeList(Arrays.asList(22, 25, 26, 300), false)) > 0);
	}
}
