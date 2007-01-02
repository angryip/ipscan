/**
 * 
 */
package net.azib.ipscan.core.values;

import net.azib.ipscan.config.Labels;

/**
 * The value for displaying in the result list, meaning that the actual value is unknown,
 * because it wasn't resolved successfully.
 *
 * @author anton
 */
public class NotAvailableValue implements Comparable {
	
	public static final NotAvailableValue INSTANCE = new NotAvailableValue();
	
	private NotAvailableValue() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		// TODO: make this configurable
		return Labels.getLabel("fetcher.value.notAvailable");
	}

	public int compareTo(Object obj) {
		if (this == obj)
			return 0;
		if (obj == null)
			return 1;
		// this value is smaller than any other object (except null)
		return -1;
	}
	
}
