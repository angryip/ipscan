/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import net.azib.ipscan.core.values.IntegerWithUnit;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.core.values.NumericRangeList;

import org.junit.Test;

/**
 * ScanningResultComparatorTest
 *
 * @author Anton Keks
 */
public class ScanningResultComparatorTest {
	
	ScanningResultComparator comparator = new ScanningResultComparator();
	
	@Test
	public void compareDifferentTypes() throws Exception {
		comparator.byIndex(0, true);

		// nulls are the same as n/a 
		assertTrue(comparator.compare(res("a"), res((String)null)) < 0);
		assertTrue(comparator.compare(res((Integer)null), res(12)) > 0);
		
		// n/s > n/a, so that n/s are always at the end of sorted data, preceded by n/a
		assertTrue(comparator.compare(res(NotAvailable.VALUE), res(NotAvailable.VALUE)) == 0);
		assertTrue(comparator.compare(res(NotScanned.VALUE), res(NotAvailable.VALUE)) > 0);
		assertTrue(comparator.compare(res(NotAvailable.VALUE), res(NotScanned.VALUE)) < 0);
		
		// n/a and n/s are at the end
		assertTrue(comparator.compare(res(125), res(NotScanned.VALUE)) < 0);
		assertTrue(comparator.compare(res(9090), res(NotAvailable.VALUE)) < 0);
		
		assertTrue(comparator.compare(res(9090), res(new NumericRangeList(Arrays.asList(9090), false))) == 0);
		assertTrue(comparator.compare(res(new NumericRangeList(Arrays.asList(154), false)), res(155)) < 0);
		assertTrue(comparator.compare(res(new NumericRangeList(Arrays.asList(1, 2, 3), false)), res(new NumericRangeList(Arrays.asList(5, 6), false))) > 0);
		
		assertTrue(comparator.compare(res("abc"), res("def")) < 0);
		assertTrue(comparator.compare(res("123"), res(123)) == 0);
		assertTrue(comparator.compare(res("ZZZ"), res(99)) > 0);
		assertTrue(comparator.compare(res("125"), res("13")) < 0);
		assertTrue(comparator.compare(res(125), res(13)) > 0);
		
		assertTrue(comparator.compare(res(new IntegerWithUnit(125, "ms")), res(new IntegerWithUnit(13, "ms"))) > 0);
	}
	
	@Test
	public void differentIndexesSupported() throws Exception {
		comparator.byIndex(0, true);
		assertTrue(comparator.compare(res("a", "z"), res("z", "a")) < 0);
		
		comparator.byIndex(1, true);
		assertTrue(comparator.compare(res("a", "z"), res("z", "a")) > 0);
		
		comparator.byIndex(2, false);
		assertTrue(comparator.compare(res("a", "z", "mmm"), res("z", "a", "mmm")) == 0);
	}
	
	@Test
	public void descendingWorks() throws Exception {
		comparator.byIndex(0, false);
		
		assertTrue(comparator.compare(res("a"), res(NotAvailable.VALUE)) < 0);
		assertTrue(comparator.compare(res(NotScanned.VALUE), res(NotAvailable.VALUE)) > 0);
		assertTrue(comparator.compare(res(2), res(1)) < 0);
		assertTrue(comparator.compare(res("A"), res("Z")) > 0);
	}
	
	@Test
	public void stringsComparedCaseInsensitively() throws Exception {
		comparator.byIndex(0, true);
		
		assertTrue(comparator.compare(res("a"), res("A")) == 0);
		assertTrue(comparator.compare(res("Anton"), res("ANT")) > 0);
		assertTrue(comparator.compare(res("Z"), res("a")) > 0);
	}
	
	@Test
	public void sortingWorksInBothDirections() throws Exception {
		ScanningResult[] results = {
			res(NotScanned.VALUE),	
			res(NotAvailable.VALUE),	
			res(15),	
			res(NotScanned.VALUE),	
			res(1),
			res(NotAvailable.VALUE),
			res("a")
		};
		
		comparator.byIndex(0, true);
		Arrays.sort(results, comparator);
		
		assertEquals(1, results[0].getValues().get(0));
		assertEquals(15, results[1].getValues().get(0));
		assertEquals("a", results[2].getValues().get(0));
		assertEquals(NotAvailable.VALUE, results[3].getValues().get(0));
		assertEquals(NotAvailable.VALUE, results[4].getValues().get(0));
		assertEquals(NotScanned.VALUE, results[5].getValues().get(0));
		assertEquals(NotScanned.VALUE, results[6].getValues().get(0));

		comparator.byIndex(0, false);
		Arrays.sort(results, comparator);
		
		assertEquals("a", results[0].getValues().get(0));
		assertEquals(15, results[1].getValues().get(0));
		assertEquals(1, results[2].getValues().get(0));
		assertEquals(NotAvailable.VALUE, results[3].getValues().get(0));
		assertEquals(NotScanned.VALUE, results[5].getValues().get(0));
	}
	
	private ScanningResult res(Object ... values) throws UnknownHostException {
		ScanningResult result = new ScanningResult(InetAddress.getLocalHost(), values.length);
		for (int i = 0; i < values.length; i++) {
			result.setValue(i, values[i]);
		}
		return result;
	}
}
