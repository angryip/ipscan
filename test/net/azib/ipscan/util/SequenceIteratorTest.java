/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * SequenceIteratorTest
 *
 * @author Anton Keks
 */
@SuppressWarnings("unchecked")
public class SequenceIteratorTest {
	
	@Test
	public void singleIterator() throws Exception {
		Iterator<Integer> i = new SequenceIterator<>(Arrays.asList(1).iterator());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void twoIterators() throws Exception {
		Iterator<Integer> i = new SequenceIterator<>(Arrays.asList(1, 2).iterator(), Arrays.asList(3).iterator());
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(2, (int)i.next());
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void firstEmpty() throws Exception {
		Iterator<Integer> i = new SequenceIterator<>(Arrays.<Integer>asList().iterator(), Arrays.asList(3).iterator());
		assertTrue(i.hasNext());
		assertEquals(3, (int)i.next());
		assertFalse(i.hasNext());
	}
}
