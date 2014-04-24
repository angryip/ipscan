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
public class NotAvailable extends Empty {
	public static final NotAvailable VALUE = new NotAvailable();
	
	private NotAvailable() {}

	/**
	 * Displays a user-friendly text string :-)
	 */
	public String toString() {
		return Config.getConfig().forScanner().notAvailableText;
	}

	@Override
	public int compareTo(Object that) {
		// n/a < n/s
		if (that == NotScanned.VALUE)
			return -sortDirection;
		return super.compareTo(that);
	}
}
