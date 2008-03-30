/**
 * 
 */
package net.azib.ipscan.core.values;

import net.azib.ipscan.config.Config;

/**
 * The value for displaying in the result list, meaning that the actual value is unknown,
 * because it was not scanned.
 *
 * @author Anton Keks
 */
public class NotScannedValue implements Comparable<Object> {
	
	public static final NotScannedValue INSTANCE = new NotScannedValue();
	
	private NotScannedValue() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		return Config.getConfig().forScanner().notScannedText;
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
