/**
 * 
 */
package net.azib.ipscan.exporters;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.azib.ipscan.config.Version;

/**
 * TXT Exporter Test
 *
 * @author Anton Keks
 */
public class TXTExporterTest extends AbstractExporterTestCase {
	
	protected Exporter createExporter() {
		return new TXTExporter();
	}
	
	@Test
	public void testPad() {
		assertEquals("a               ", ((TXTExporter)exporter).pad("a", 1));
		assertEquals("                ", ((TXTExporter)exporter).pad("", 0));
		assertEquals("abc                 ", ((TXTExporter)exporter).pad("abc", 20));
		assertEquals("                ", ((TXTExporter)exporter).pad(null, 5));
		assertEquals("5               ", ((TXTExporter)exporter).pad(new Integer(5), 5));
	}
	
	@Test
	public void testHeaderWithoutAppend() throws IOException {
		exporter.start(outputStream, "feederstuff");
		exporter.end();
		assertContains(Version.FULL_NAME);
		assertContains(Version.WEBSITE);
	}

	@Test
	public void testHeaderWithAppend() throws IOException {
		exporter.setAppend(true);
		exporter.start(outputStream, "feederstuff");
		exporter.end();
		assertNotContains(Version.FULL_NAME);
		assertNotContains(Version.WEBSITE);
	}

	@Test
	public void testFetchersWithAppend() throws IOException {
		super.testFetchersWithAppend();
		assertTrue(((TXTExporter)exporter).padLengths != null);		
	}

	@Test
	public void testFetchersWithoutAppend() throws IOException {
		super.testFetchersWithoutAppend();
		assertTrue(((TXTExporter)exporter).padLengths != null);		
	}
	
	@Test
	public void testFeederInfo() throws IOException {
		exporter.start(outputStream, "192.168.1.1 - 192.168.3.255");
		exporter.end();
		assertContains("192.168.1.1 - 192.168.3.255");
	}
	
}
