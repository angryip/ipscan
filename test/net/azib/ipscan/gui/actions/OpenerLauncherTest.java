/**
 * 
 */
package net.azib.ipscan.gui.actions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.UserErrorException;

import org.junit.Before;
import org.junit.Test;

/**
 * OpenerLauncherTest
 *
 * @author anton
 */
public class OpenerLauncherTest {

	@Before
	public void setUp() {
		Config.initialize();
	}

	@Test
	public void testReplaceValues() {
		FetcherRegistry fetcherRegistry = createMock(FetcherRegistry.class);
		expect(fetcherRegistry.getSelectedFetcherIndex("fetcher.ip")).andReturn(0).times(3);
		expect(fetcherRegistry.getSelectedFetcherIndex("fetcher.hostname")).andReturn(1);
		expect(fetcherRegistry.getSelectedFetcherIndex("fetcher.comment")).andReturn(2);
		expect(fetcherRegistry.getSelectedFetcherIndex("noSuchFetcher")).andReturn(-1);
		replay(fetcherRegistry);
		
		OpenerLauncher ol = new OpenerLauncher(fetcherRegistry, null) {
			String getScannedValue(int selectedItem, int fetcherIndex) {
				switch (fetcherIndex) {
					case 0:
						return "127.0.0.1";
					case 1:
						return "HOSTNAME";
					default:
						return null;
				}
			}
		};
		
		assertEquals("\\\\127.0.0.1", ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0));
		assertEquals("HOSTNAME$$$127.0.0.1xxx${}", ol.prepareOpenerStringForItem("${fetcher.hostname}$$$${fetcher.ip}xxx${}", 0));
		assertEquals("http://127.0.0.1:80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0));
		
		try {
			ol.prepareOpenerStringForItem("${noSuchFetcher}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.unknownFetcher") + "noSuchFetcher", e.getMessage());
		}

		try {
			ol.prepareOpenerStringForItem("${fetcher.comment}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.comment", e.getMessage());
		}
		
		verify(fetcherRegistry);
	}
}
