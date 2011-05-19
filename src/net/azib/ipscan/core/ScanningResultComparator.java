/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.util.Comparator;

import net.azib.ipscan.core.values.Empty;
import net.azib.ipscan.core.values.NotAvailable;

public class ScanningResultComparator implements Comparator<ScanningResult> {
	
	private int index;
	private boolean ascending;

	@SuppressWarnings("unchecked")
	public int compare(ScanningResult r1, ScanningResult r2) {
		Object val1 = r1.getValues().get(index);
		Object val2 = r2.getValues().get(index);
		
		if (val1 == null) 
			val1 = NotAvailable.VALUE;
		if (val2 == null) 
			val2 = NotAvailable.VALUE;

		int result;
		if (val1 == val2) {
			result = 0;
		}
		else
		if (val1.getClass() == val2.getClass() && !(val1 instanceof String) && val1 instanceof Comparable) {
			// both are the same type and Comparable
			result = ((Comparable)val1).compareTo(val2);
		}
		else {
			if (val1 instanceof Empty)
				result = ((Empty)val1).compareTo(val2);
			else
			if (val2 instanceof Empty)
				result = -((Empty)val2).compareTo(val1);
			else {
				// otherwise compare String representations
				result = val1.toString().compareToIgnoreCase(val2.toString());
			}
		}
		
		if (result == 0 && index != 0) {
			// if values are equal, order them according to the IPs
			result = ((Comparable)r1.getValues().get(0)).compareTo(r2.getValues().get(0));
		}
		
		return result * (ascending ? 1 : -1);
	}

	public void byIndex(int index, boolean ascending) {
		this.index = index;
		this.ascending = ascending;

		// this ensures that all Empty objects are always at the end
		Empty.setSortDirection(ascending);
	}
}
