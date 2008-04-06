/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

/**
 * SequenceIteratorTest
 *
 * @author Anton Keks
 */
@SuppressWarnings("unchecked")
public class SequenceIteratorTest {
	
	@Test
	public void singleIterator() throws Exception {
		Iterator<Integer> i = new SequenceIterator<Integer>(Arrays.asList(1).iterator());
		assertTrue(i.hasNext());
		assertEquals(1, i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void twoIterators() throws Exception {
		Iterator<Integer> i = new SequenceIterator<Integer>(Arrays.asList(1, 2).iterator(), Arrays.asList(3).iterator());
		assertTrue(i.hasNext());
		assertEquals(1, i.next());
		assertTrue(i.hasNext());
		assertEquals(2, i.next());
		assertTrue(i.hasNext());
		assertEquals(3, i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void firstEmpty() throws Exception {
		Iterator<Integer> i = new SequenceIterator<Integer>(Arrays.<Integer>asList().iterator(), Arrays.asList(3).iterator());
		assertTrue(i.hasNext());
		assertEquals(3, i.next());
		assertFalse(i.hasNext());
	}
}
