/**
 * 
 */
package net.azib.ipscan.core.values;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * NumericListValue - a value object containing a list of numbers.
 * 
 * TODO: add parsing functionality here.
 *
 * @author Anton Keks
 */
public class NumericListValue extends TreeSet {
	
	private static final long serialVersionUID = 1L;
	
	private boolean displayAsRanges; // TODO: make configurable
	
	/**
	 * Creates a new empty instance
	 * @param displayAsRanges whether toString() outputs all number or their ranges
	 */
	public NumericListValue(boolean displayAsRanges) {
		super();
		this.displayAsRanges = displayAsRanges;
	}

	/**
	 * Creates a new instance initialized with the following numbers.
	 * @param numbers Set of numbers 
	 * @param displayAsRanges whether toString() outputs all number or their ranges 
	 */
	public NumericListValue(Collection numbers, boolean displayAsRanges) {
		super(numbers);
		this.displayAsRanges = displayAsRanges;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		Iterator i = iterator();
		Integer prevPort = new Integer(Integer.MAX_VALUE);
		boolean isRange = false;
		
		if (i.hasNext()) {
			prevPort = (Integer) i.next();
			sb.append(prevPort);
		}
		
		while (i.hasNext()) {
			Integer port = (Integer) i.next();
			
			if (displayAsRanges && prevPort.intValue() + 1 == port.intValue()) {
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
