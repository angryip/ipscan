/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
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
 * @author Anton Keks
 */
public class NumericRangeList implements Comparable<NumericRangeList> {
	
	private static final long serialVersionUID = 1L;
	
	private boolean displayAsRanges; // TODO: make configurable
	private int[] numbers;
	
	/**
	 * Creates a new instance initialized with the following numbers.
	 * @param numbers Collections of Numbers (must be sorted for ranges to work) 
	 * @param displayAsRanges whether toString() outputs all number or their ranges 
	 */
	public NumericRangeList(Collection<Integer> numbers, boolean displayAsRanges) {
		// copy numbers to an array (unfortunately toArray() cannot be used because int[] is not an Object[])
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
		StringBuilder sb = new StringBuilder();
		
		int prevNumber = Integer.MAX_VALUE;
		int rangeStartNumber = 0;
		boolean isRange = false;		
		int i = 0;
		
		if (numbers.length > 0) {
			prevNumber = numbers[0];
			sb.append(prevNumber);
		}
		
		while (++i < numbers.length) {
			int curNumber = numbers[i];
			
			if (displayAsRanges && prevNumber + 1 == curNumber) {
				if (!isRange) {
					isRange = true;
					rangeStartNumber = prevNumber;
				}
			}
			else {
				if (isRange) {
					// display short ranges with comma, long ranges with dash
					sb.append(rangeStartNumber+1 == prevNumber ? ',' : '-').append(prevNumber);
					isRange = false;
				}
				sb.append(',').append(curNumber);
			}
			prevNumber = curNumber;
		}
		
		if (isRange) {
			sb.append(rangeStartNumber+1 == prevNumber ? ',' : '-').append(prevNumber);
		}
		
		return sb.toString();
	}

	public int compareTo(NumericRangeList that) {
		// compare length
		int result = this.numbers.length - that.numbers.length;
		if (result == 0) {
			// compare contents if length is the same
			for (int i = 0; i < this.numbers.length && result == 0; i++) {
				result = this.numbers[i] - that.numbers[i];
			}
		}
		return result;
	}
}
