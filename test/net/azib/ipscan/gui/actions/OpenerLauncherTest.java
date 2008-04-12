/**
 * 
 */
package net.azib.ipscan.gui.actions;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.values.InetAddressHolder;
import net.azib.ipscan.core.values.IntegerWithUnit;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.HostnameFetcher;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PingFetcher;

import org.junit.Test;

/**
 * OpenerLauncherTest
 *
 * @author Anton Keks
 */
public class OpenerLauncherTest {

	@Test
	public void testReplaceValues() throws UnknownHostException {
		FetcherRegistry fetcherRegistry = createMock(FetcherRegistry.class);
		expect(fetcherRegistry.getSelectedFetchers()).andReturn(Collections.<Fetcher>nCopies(5, null)).times(2);
		expect(fetcherRegistry.getSelectedFetcherIndex(IPFetcher.ID)).andReturn(0).times(3);
		expect(fetcherRegistry.getSelectedFetcherIndex(HostnameFetcher.ID)).andReturn(1).anyTimes();
		expect(fetcherRegistry.getSelectedFetcherIndex(PingFetcher.ID)).andReturn(2);
		expect(fetcherRegistry.getSelectedFetcherIndex("fetcher.comment")).andReturn(3).anyTimes();
		expect(fetcherRegistry.getSelectedFetcherIndex("noSuchFetcher")).andReturn(-1);
		replay(fetcherRegistry);
		
		ScanningResultList scanningResults = new ScanningResultList(fetcherRegistry);
		scanningResults.initNewScan(createMockFeeder("info"));
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("127.0.0.1"));
		result.setValue(0, new InetAddressHolder(InetAddress.getByName("127.0.0.1")));
		result.setValue(1, "HOSTNAME");
		result.setValue(2, new IntegerWithUnit(10, "ms"));
		scanningResults.registerAtIndex(0, result);
		
		OpenerLauncher ol = new OpenerLauncher(fetcherRegistry, scanningResults);
		
		assertEquals("\\\\127.0.0.1", ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0));
		assertEquals("HOSTNAME$$$127.0.0.1xxx${}", ol.prepareOpenerStringForItem("${fetcher.hostname}$$$${fetcher.ip}xxx${}", 0));
		assertEquals("http://127.0.0.1:80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0));
		assertEquals(result.getValues().get(2) + ", xx", ol.prepareOpenerStringForItem("${fetcher.ping}, xx", 0));
				
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

		try {
			result.setValue(3, NotAvailable.VALUE);
			ol.prepareOpenerStringForItem("${fetcher.comment}", 0);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.comment", e.getMessage());
		}
		
		result.setValue(1, null);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0));
		result.setValue(1, NotAvailable.VALUE);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0));
		
		verify(fetcherRegistry);
	}
	
	@Test
	public void testCommandSplitting() throws Exception {
		assertArrayEquals(new String[] {"hello", "world"}, OpenerLauncher.splitCommand("hello world"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo 'hello world' muha-ha"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo \"hello world\" muha-ha"));
		assertArrayEquals(new String[] {"mix \"1", "mix '2"}, OpenerLauncher.splitCommand("'mix \"1' \"mix '2\""));
		assertArrayEquals(new String[] {"\"aaa"}, OpenerLauncher.splitCommand("\"aaa"));
	}
	
	private Feeder createMockFeeder(String feederInfo) {
		Feeder feeder = createMock(Feeder.class);
		expect(feeder.getInfo()).andReturn(feederInfo);
		expect(feeder.getName()).andReturn("feeder.range");
		replay(feeder);
		return feeder;
	}
}
