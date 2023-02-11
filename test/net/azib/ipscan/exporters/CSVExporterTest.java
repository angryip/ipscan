package net.azib.ipscan.exporters;

import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

/**
 * CSV Exporter Test
 *
 * @author Anton Keks
 */
public class CSVExporterTest extends AbstractExporterTestCase {
	
	protected Exporter createExporter() {
		return new CSVExporter();
	}	
	
	@Test
	public void testCSVSafeString() {
		assertEquals(".a.bb.c.d.", ((CSVExporter)exporter).csvSafeString(",a,bb,c,d,"));
		assertEquals("", ((CSVExporter)exporter).csvSafeString(""));
		assertEquals("uuuuhha;", ((CSVExporter)exporter).csvSafeString("uuuuhha;"));
		assertEquals("", ((CSVExporter)exporter).csvSafeString(null));
		assertEquals("123", ((CSVExporter)exporter).csvSafeString(123L));
	}
	
	@Test
	public void testFetchersWithoutAppend() throws IOException {
		exporter.start(outputStream, null);
		exporter.setFetchers(new String[] {"fet1", "hello2", "Mega Fetcher", "oops, comma here"});
		exporter.end();
		assertContains("fet1");
		assertContains("Mega Fetcher");
		assertContains("oops. comma here");
	}
	
	@Test
	public void testNextAddressResults() throws IOException {
		exporter.start(outputStream, null);
		exporter.setFetchers(new String[] {"fet1", "hello2"});
		exporter.nextAddressResults(new Object[] {InetAddress.getLocalHost(), "oops, comma"});
		exporter.end();
		assertContains("oops. comma");
	}

}
