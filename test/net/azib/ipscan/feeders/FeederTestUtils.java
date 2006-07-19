/**
 * 
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;
import junit.framework.Assert;

/**
 * FeederTestUtils
 *
 * @author anton
 */
public class FeederTestUtils {
	
	public static void assertFeederException(String message, FeederException e) {
		// assert that the message is correct
		Assert.assertEquals(e.getMessage(), message);
		// check that corresponding label exists
		Assert.assertNotNull(Labels.getInstance().getString("exception.FeederException." + message));
	}

}
