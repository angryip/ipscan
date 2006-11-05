/**
 * 
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.FetcherRegistryImpl;
import net.azib.ipscan.gui.UserErrorException;
import junit.framework.TestCase;

/**
 * OpenerLauncherTest
 *
 * @author anton
 */
public class OpenerLauncherTest extends TestCase {

	public void testReplaceValues() {
		Config.initialize();
		OpenerLauncher ol = new OpenerLauncher(new FetcherRegistryImpl(), null) {
			String getScannedValue(int selectedItem, int fetcherIndex) {
				switch (fetcherIndex) {
					case 0:
						return "127.0.0.1";
					case 1:
						return "PING";
					default:
						return null;
				}
			}
		};
		
		assertEquals("\\\\127.0.0.1", ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0));
		assertEquals("PING$$$127.0.0.1xxx${}", ol.prepareOpenerStringForItem("${fetcher.ping}$$$${fetcher.ip}xxx${}", 0));
		assertEquals("http://127.0.0.1:80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0));
		
		try {
			ol.prepareOpenerStringForItem("${noSuchFetcher}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.unknownFetcher") + "noSuchFetcher", e.getMessage());
		}

		try {
			ol.prepareOpenerStringForItem("${fetcher.ping.ttl}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.ping.ttl", e.getMessage());
		}
	}
}
