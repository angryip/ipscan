/**
 * 
 */
package net.azib.ipscan.core.values;

import net.azib.ipscan.config.Config;

/**
 * The value for displaying in the result list, meaning that the actual value is unknown,
 * because it wasn't resolved successfully.
 *
 * @author Anton Keks
 */
public class NotAvailableValue implements Comparable<Object> {
	
	public static final NotAvailableValue INSTANCE = new NotAvailableValue();
	
	private NotAvailableValue() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		return Config.getGlobal().notAvailableText;
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
