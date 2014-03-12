/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.values;

/**
 * Base class for values that contain no embedded value, e.g. n/a, n/s
 *
 * @author Anton Keks
 */
public abstract class Empty implements Comparable<Object> {
	
	static int sortDirection = 1;
	
	/**
	 * @param ascending changes the sorting behavior of all Empty objects,
	 * passing true here will make all Empty objects to be greater than any other objects,
	 * passing false will do otherwise. This needs to be set to make all Empty objects always
	 * appear at the end of the sorted list.
	 */
	public static void setSortDirection(boolean ascending) {
		Empty.sortDirection = ascending ? 1 : -1;
	}
	
	public int compareTo(Object that) {
		if (this == that)
			return 0;
		// this value is either smaller or greater than any other object (except null)
		return sortDirection;
	}
}
