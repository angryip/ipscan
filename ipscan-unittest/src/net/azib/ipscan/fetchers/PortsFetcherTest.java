/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import junit.framework.TestCase;

/**
 * PortsFetcherTest
 *
 * @author anton
 */
public class PortsFetcherTest extends TestCase {

	public void testPortListToRange() {
		assertEquals("", PortsFetcher.portListToRange(Collections.EMPTY_LIST, true));
		assertEquals("1", PortsFetcher.portListToRange(Arrays.asList(new Object[] {new Integer(1)}), true));
		assertEquals("1-3", PortsFetcher.portListToRange(Arrays.asList(new Object[] {new Integer(1), new Integer(2), new Integer(3)}), true));
		assertEquals("1-3", PortsFetcher.portListToRange(new TreeSet(Arrays.asList(new Object[] {new Integer(2), new Integer(3), new Integer(1)})), true));
		assertEquals("1,2,3", PortsFetcher.portListToRange(Arrays.asList(new Object[] {new Integer(1), new Integer(2), new Integer(3)}), false));
		assertEquals("1,5-6,15", PortsFetcher.portListToRange(Arrays.asList(new Object[] {new Integer(1), new Integer(5), new Integer(6), new Integer(15)}), true));
	}
}
