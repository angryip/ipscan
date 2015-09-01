/**
 * 
 */
package net.azib.ipscan.exporters;

import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
		assertContains(Version.NAME);
		assertContains(Version.WEBSITE);
	}

	@Test
	public void testHeaderWithAppend() throws IOException {
		exporter.shouldAppendTo(null);
		exporter.start(outputStream, "feederstuff");
		exporter.end();
		assertNotContains(Version.NAME);
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

	@Test
	public void importFromFile() throws Exception {
		String file = getClass().getResource("import.txt").getPath();
		AbstractFeederGUI feederGUI = mock(AbstractFeederGUI.class);

		List<ScanningResult> results = ((TXTExporter) exporter).importResults(file, feederGUI);

		assertEquals(7, results.size());
		verify(feederGUI).unserialize("192.168.0.19", "192.168.0.255");
	}
}
