/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.util;

import java.util.Iterator;

/**
 * SequenceIterator - joins several iterators together to provide a seamless iteration.
 *
 * @author Anton Keks
 */
public class SequenceIterator<E> implements Iterator<E> {
	private Iterator<E>[] iterators;
	int currentIndex = 0;
	
	public SequenceIterator(Iterator<E>... iterators) {
		this.iterators = iterators;
		
		// check that last iterator is not empty (otherwise the code below won't work)
		if (!iterators[iterators.length-1].hasNext())
			throw new IllegalArgumentException();
	}

	public boolean hasNext() {
		// combined iterator has elements until the last iterator has them
		return iterators[iterators.length-1].hasNext();
	}

	public E next() {
		// take the next iterator if current ran out of elements
		if (!iterators[currentIndex].hasNext())
			currentIndex++;
		
		return iterators[currentIndex].next();
	}

	public void remove() {
		iterators[currentIndex].remove();
	}
}
