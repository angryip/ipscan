/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.values;

import java.util.Collection;

/**
 * NumericListValue - a value object containing a list of numbers.
 * Note: it is immutable. 
 * TODO: cache immutable number arrays
 * 
 * TODO: add parsing functionality here.
 *
 * @author Anton Keks Keks
 */
public class NumericListValue {
	
	private static final long serialVersionUID = 1L;
	
	private boolean displayAsRanges; // TODO: make configurable
	private int[] numbers;
	
	/**
	 * Creates a new instance initialized with the following numbers.
	 * @param numbers Collections of Numbers (must be sorted for ranges to work) 
	 * @param displayAsRanges whether toString() outputs all number or their ranges 
	 */
	public NumericListValue(Collection<Integer> numbers, boolean displayAsRanges) {
		// copy numbers to an array (unfortunately toArray() cannot be used because int[] is not IS-A Object[])
		this.numbers = new int[numbers.size()];
		int c = 0;
		for (Number n : numbers) {			
			this.numbers[c++] = n.intValue();
		}
		
		this.displayAsRanges = displayAsRanges;
	}
	
	/**
	 * Outputs nice, human-friendly numeric list, displayed either as ranges or fully
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		int prevPort = Integer.MAX_VALUE;
		boolean isRange = false;		
		int i = 0;
		
		if (numbers.length > 0) {
			prevPort = numbers[0];
			sb.append(prevPort);
		}
		
		while (++i < numbers.length) {
			int port = numbers[i];
			
			if (displayAsRanges && prevPort + 1 == port) {
				isRange = true;
			}
			else {
				if (isRange) {
					sb.append('-').append(prevPort);
					isRange = false;
				}
				sb.append(',').append(port);
			}
			prevPort = port;
		}
		
		if (isRange) {
			sb.append('-').append(prevPort);
		}
		
		return sb.toString();
	}
}
