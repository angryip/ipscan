/**
 * 
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.UserErrorException;
import net.azib.ipscan.gui.actions.CommandsActions.SelectOpener;
import junit.framework.TestCase;

/**
 * CommandsActionsTest
 *
 * @author anton
 */
public class CommandsActionsTest extends TestCase {

	public void testReplaceValues() {
		Config.initialize();
		CommandsActions.SelectOpener so = new SelectOpener(null) {
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
		
		assertEquals("\\\\127.0.0.1", so.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0));
		assertEquals("PING$$$127.0.0.1xxx${}", so.prepareOpenerStringForItem("${fetcher.ping}$$$${fetcher.ip}xxx${}", 0));
		assertEquals("http://127.0.0.1:80/www", so.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0));
		
		try {
			so.prepareOpenerStringForItem("${noSuchFetcher}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getInstance().getString("exception.UserErrorException.opener.unknownFetcher") + "noSuchFetcher", e.getMessage());
		}

		try {
			so.prepareOpenerStringForItem("${fetcher.ping.ttl}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getInstance().getString("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.ping.ttl", e.getMessage());
		}
	}
}
