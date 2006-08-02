/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

import net.azib.ipscan.config.Labels;

/**
 * IP List Exporter Test
 *
 * @author anton
 */
public class IPListExporterTest extends AbstractExporterTestCase {
	
	protected void setUp() throws Exception {
		Labels.initialize(Locale.ENGLISH);
		super.setUp();
	}

	protected Exporter createExporter() {
		return new IPListExporter();
	}
	
	public void testBasic() throws IOException {
		Labels labels = Labels.getInstance();
		
		exporter.start(outputStream, "feederstuff");		
		exporter.setFetchers(new String[] {"fetcher1", labels.getString("fetcher.ip"), "mega long fetcher 2", labels.getString("fetcher.ports")});
		exporter.nextAdressResults(new Object[] {"", "123", "", "1,23; 4-6 78"});
		exporter.end();
		
		assertContains("123:1");
		assertContains("123:23");
		assertContains("123:4");		
		assertContains("123:5");		
		assertContains("123:6");		
		assertContains("123:78");		
	}

	public void testFetchersWithAppend() throws IOException {
		// default implementation doesn't work
	}
	
	
	public void testFetchersWithoutAppend() throws IOException {
		// default implementation doesn't work
	}
	
	public void testNextAddressResultsWithNulls() throws IOException {
		Labels labels = Labels.getInstance();

		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {labels.getString("fetcher.ip"), "fetcher1", labels.getString("fetcher.ports")});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost(), null, null});
		exporter.end();
	}

	public void testFindFetcherByLabel() {
		Labels labels = Labels.getInstance();
		
		assertEquals(0, IPListExporter.findFetcherByLabel("fetcher.ip", new String[] {labels.getString("fetcher.ip")}));
		assertEquals(3, IPListExporter.findFetcherByLabel("fetcher.ip", new String[] {"a", "b", "c", labels.getString("fetcher.ip")}));
		assertEquals(1, IPListExporter.findFetcherByLabel("fetcher.ports", new String[] {labels.getString("fetcher.ports") + "x", labels.getString("fetcher.ports"), "mmmm"}));
		
		try {
			IPListExporter.findFetcherByLabel("fetcher.ip", new String[] {"1", "2"});
			fail();
		}
		catch (ExporterException e) {
			assertEquals("fetcher.notFound", e.getMessage());
		}
	}
}
