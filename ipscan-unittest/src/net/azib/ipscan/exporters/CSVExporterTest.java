/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.net.InetAddress;

/**
 * CSV Exporter Test
 *
 * @author anton
 */
public class CSVExporterTest extends AbstractExporterTestCase {
	
	protected Exporter createExporter() {
		return new CSVExporter();
	}	
	
	public void testCSVSafeString() {
		assertEquals(".a.bb.c.d.", ((CSVExporter)exporter).csvSafeString(",a,bb,c,d,"));
		assertEquals("", ((CSVExporter)exporter).csvSafeString(""));
		assertEquals("uuuuhha;", ((CSVExporter)exporter).csvSafeString("uuuuhha;"));
		assertEquals("", ((CSVExporter)exporter).csvSafeString(null));
		assertEquals("123", ((CSVExporter)exporter).csvSafeString(new Long(123)));
	}
	
	public void testFetchersWithoutAppend() throws IOException {
		exporter.start(outputStream, null);
		exporter.setFetchers(new String[] {"fet1", "hello2", "Mega Fetcher", "oops, comma here"});
		exporter.end();
		assertContains("fet1");
		assertContains("Mega Fetcher");
		assertContains("oops. comma here");
	}
	
	public void testNextAddressResults() throws IOException {
		exporter.start(outputStream, null);
		exporter.setFetchers(new String[] {"fet1", "hello2"});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost(), "oops, comma"});
		exporter.end();
		assertContains("oops. comma");
	}

}
