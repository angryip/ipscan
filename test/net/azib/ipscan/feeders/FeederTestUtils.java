/**
 * 
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;
import junit.framework.Assert;

/**
 * FeederTestUtils
 *
 * @author Anton Keks
 */
public class FeederTestUtils {
	
	public static void assertFeederException(String message, FeederException e) {
		// assert that the message is correct
		Assert.assertEquals(message, e.getMessage());
		// check that corresponding label exists
		Assert.assertNotNull(Labels.getLabel("exception.FeederException." + message));
	}

}
