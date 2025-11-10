package net.azib.ipscan.exporters;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NumericRangeList;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * IP List Exporter Test
 *
 * @author Anton Keks
 */
public class IPListExporterTest extends AbstractExporterTestCase {
	
	@Before
	public void setUp() throws Exception {
		Labels.initialize(Locale.ENGLISH);
		super.setUp();
	}

	protected Exporter createExporter() {
		return new IPListExporter();
	}
	
	@Test
	public void testBasic() throws IOException {
		var labels = Labels.getInstance();
		
		exporter.start(outputStream, "feederstuff");		
		exporter.setFetchers(new String[] {"fetcher1", labels.get(IPFetcher.ID), "mega long fetcher 2", labels.get(PortsFetcher.ID)});
		exporter.nextAddressResults(new Object[] {"", "123", "", new NumericRangeList(Arrays.asList(1,23,4,5,6,78), true)});
		exporter.end();
		
		assertContains("123:1");
		assertContains("123:23");
		assertContains("123:4");		
		assertContains("123:5");		
		assertContains("123:6");		
		assertContains("123:78");		
	}

	@Test
	public void testFetchersWithAppend() throws IOException {
		// default implementation doesn't work
	}
	
	@Test
	public void testFetchersWithoutAppend() throws IOException {
		// default implementation doesn't work
	}
	
	@Test
	public void testNextAddressResultsWithNullsOrEmptyValues() throws IOException {
		var labels = Labels.getInstance();

		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {labels.get(IPFetcher.ID), "fetcher1", labels.get(PortsFetcher.ID)});
		exporter.nextAddressResults(new Object[] {InetAddress.getLocalHost(), null, null});
		exporter.nextAddressResults(new Object[] {InetAddress.getLocalHost(), null, NotAvailable.VALUE});
		exporter.end();
	}
	
	@Test
	public void testFindFetcherById() {
		var labels = Labels.getInstance();
		
		assertEquals(0, IPListExporter.findFetcherById(IPFetcher.ID, new String[] {labels.get(IPFetcher.ID)}));
		assertEquals(3, IPListExporter.findFetcherById(IPFetcher.ID, new String[] {"a", "b", "c", labels.get(IPFetcher.ID)}));
		assertEquals(1, IPListExporter.findFetcherById(PortsFetcher.ID, new String[] {labels.get(PortsFetcher.ID) + "x", labels.get(PortsFetcher.ID), "mmmm"}));
		
		try {
			IPListExporter.findFetcherById(IPFetcher.ID, new String[] {"1", "2"});
			fail();
		}
		catch (ExporterException e) {
			assertEquals("fetcher.notFound", e.getMessage());
		}
	}
}
