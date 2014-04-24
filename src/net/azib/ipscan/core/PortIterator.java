/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.util.Iterator;


/**
 * A class for iteration of ports, specified in special format, like:
 * 1,5-7,35-40
 *
 * @author Anton Keks
 */
public final class PortIterator implements Iterator<Integer>, Cloneable {
	
	private int[] portRangeStart;
	private int[] portRangeEnd;
	
	private int rangeCountMinus1;
	private int rangeIndex;
	private int currentPort;
	
	private boolean hasNext;
	
	/**
	 * Constructs the PortIterator instance
	 * @param portString the port string to parse
	 */
	public PortIterator(String portString) {
		if (portString != null && (portString = portString.trim()).length() > 0) {
			String[] portRanges = portString.split("[\\s\t\n\r,;]+");
			
			// initialize storage
			portRangeStart = new int[portRanges.length+1];	// +1 for optimization of 'next' method, prevents ArrayIndexOutOfBoundsException
			portRangeEnd = new int[portRanges.length];
	
			// parse ints
			for (int i = 0; i < portRanges.length; i++) {
				String range = portRanges[i];
				int dashPos = range.indexOf('-') + 1;
				int endPort = Integer.parseInt(range.substring(dashPos));
				portRangeEnd[i] = endPort;
				portRangeStart[i] = dashPos == 0 ? endPort : Integer.parseInt(range.substring(0, dashPos-1));
				if (endPort <= 0 || endPort >= 65536) {
					throw new NumberFormatException(endPort + " port is out of range");
				}
			}
			
			currentPort = portRangeStart[0];
			rangeCountMinus1 = portRanges.length - 1;
			hasNext = rangeCountMinus1 >= 0;
		}
	}
	
	/**
	 * @return true if there are more ports left
	 */
	public boolean hasNext() {
		return hasNext;
	}
	
	/**
	 * @return next port number
	 */
	public Integer next() {
		int returnPort = currentPort++;
		
		if (currentPort > portRangeEnd[rangeIndex]) {
			hasNext = rangeIndex < rangeCountMinus1;
			rangeIndex++;
			currentPort = portRangeStart[rangeIndex];
		}
		
		return returnPort;
	}
	
	public int size() {
		int size = 0;
		if (portRangeStart != null) {
			for (int i = 0; i <= rangeCountMinus1; i++) {
				size += portRangeEnd[i] - portRangeStart[i] + 1;
			}
		}
		return size;
	}

	/**
	 * Clones the PortIterator instance.
	 * @return a shallow copy
	 */
	public PortIterator copy() {
		try {
			return (PortIterator) super.clone();
		}
		catch (CloneNotSupportedException e) {
			assert false : "this should never happen";
			return null;
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
