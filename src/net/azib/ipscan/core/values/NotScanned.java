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
public class NotScanned extends Empty {
	public static final NotScanned VALUE = new NotScanned();
	
	private NotScanned() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		return Config.getConfig().forScanner().notScannedText;
	}

	@Override
	public int compareTo(Object that) {
		// n/s > n/a
		if (that == NotAvailable.VALUE)
			return sortDirection;
		return super.compareTo(that);
	}
}
