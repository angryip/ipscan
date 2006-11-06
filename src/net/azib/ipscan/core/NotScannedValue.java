/**
 * 
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.Labels;

/**
 * The value for displaying in the result list, meaning that the actual value is unknown,
 * because it was not scanned.
 *
 * @author anton
 */
public class NotScannedValue implements Comparable {
	
	public static final NotScannedValue INSTANCE = new NotScannedValue();
	
	private NotScannedValue() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		// TODO: make this configurable
		return Labels.getLabel("fetcher.value.notScanned");
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
