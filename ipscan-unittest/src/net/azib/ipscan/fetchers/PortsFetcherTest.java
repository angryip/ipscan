/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;

/**
 * PortsFetcherTest
 *
 * @author anton
 */
public class PortsFetcherTest {

	@Test
	public void testPortListToRange() {
		assertEquals("", PortsFetcher.portListToRange(Collections.EMPTY_LIST, true));
		assertEquals("1", PortsFetcher.portListToRange(Arrays.asList(new Object[] {1}), true));
		assertEquals("1-3", PortsFetcher.portListToRange(Arrays.asList(new Object[] {1, 2, 3}), true));
		assertEquals("1-3", PortsFetcher.portListToRange(new TreeSet<Integer>(Arrays.asList(new Integer[] {2, 3, 1})), true));
		assertEquals("1,2,3", PortsFetcher.portListToRange(Arrays.asList(new Object[] {1, 2, 3}), false));
		assertEquals("1,5-6,15", PortsFetcher.portListToRange(Arrays.asList(new Object[] {1, 5, 6, 15}), true));
	}
}
