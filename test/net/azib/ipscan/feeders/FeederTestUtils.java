package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * FeederTestUtils
 *
 * @author Anton Keks
 */
public class FeederTestUtils {
	public static void assertFeederException(String message, FeederException e) {
		// assert that the message is correct
		assertEquals(message, e.getMessage());
		// check that corresponding label exists
		assertNotNull(Labels.getLabel("exception.FeederException." + message));
	}
}
