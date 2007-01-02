/**
 * 
 */
package net.azib.ipscan.core.values;

import net.azib.ipscan.config.Labels;

/**
 * IntegerWithUnit - an Integer value together with a unit, e.g. "10 ms".
 * TODO: IntegerWithUnitTest
 *
 * @author anton
 */
public class IntegerWithUnit implements Comparable {
	
	private int value;
	private String unitLabel;
	
	public IntegerWithUnit(int value, String unitLabel) {
		this.value = value;
		this.unitLabel = unitLabel;
	}
	
	public int intValue() {
		return value;
	}
	
	public String toString() {
		return value + Labels.getLabel(unitLabel);
	}

	public int hashCode() {
		return value;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof IntegerWithUnit)
			return value == ((IntegerWithUnit) obj).value;
		return false;
	}

	public int compareTo(Object obj) {
		if (this == obj)
			return 0;
		if (obj == null)
			return 1;
		int other = ((IntegerWithUnit) obj).value;
		return value == other ? 0 : value > other ? 1 : -1;
	}

}
