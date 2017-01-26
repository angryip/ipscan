package net.azib.ipscan.feeders;

import junit.framework.Assert;
import net.azib.ipscan.config.Labels;

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
