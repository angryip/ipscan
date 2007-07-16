/**
 * 
 */
package net.azib.ipscan.exporters;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import net.azib.ipscan.config.Labels;
import junit.framework.ComparisonFailure;

import org.junit.Before;
import org.junit.Test;

/**
 * TestCase for Exporters.
 * It contains initialization and generic tests for any Exporter.
 *
 * @author Anton Keks
 */
public abstract class AbstractExporterTestCase {
	
	protected Exporter exporter;
	protected ByteArrayOutputStream outputStream;
	protected String outputContent;
	
	protected abstract Exporter createExporter();

	@Before
	public void setUp() throws Exception {
		outputStream = new ByteArrayOutputStream();
		exporter = createExporter();
	}

	@Test
	public void testLabel() {
		assertNotNull(Labels.getLabel(exporter.getLabel()));
	}

	@Test
	public void testFilenameExtension() {
		assertNotNull(exporter.getFilenameExtension());
	}
	
	@Test
	public void testStreamFlushAndClose() throws IOException {
		final boolean wasClosed[] = new boolean[] {false, false};
		Exporter exporter2 = createExporter();
		OutputStream mockOutputStream = new OutputStream() {
			public void write(int b) throws IOException {
			}
			public void close() throws IOException {
				wasClosed[0] = true;
			}
			public void flush() {
				wasClosed[1] = true;
			}
		};
		exporter2.start(mockOutputStream, "feederstuff");
		// output something to ensure that the flush will be called
		exporter2.setFetchers(new String[] {Labels.getLabel("fetcher.ip"), Labels.getLabel("fetcher.ports")});
		exporter2.nextAdressResults(new Object[] {"1", "2"});
		// this should invoke flush among other things
		exporter2.end();
		// close: no
		assertFalse(wasClosed[0]);
		// flush: yes
		assertTrue(wasClosed[1]);
	}
	
	@Test
	public void testBasic() throws Exception {
		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {"IP", "hello", "fetcher2"});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost().getHostAddress(), "world", new Integer(53)});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost().getHostAddress(), "buga", new Integer(-1)});
		exporter.end();
		assertContains(InetAddress.getLocalHost().getHostAddress());
		assertContains("hello");
		assertContains("fetcher2");
		assertContains("world");
		assertContains("53");
		assertContains("buga");
		assertContains("-1");
	}
		
	@Test
	public void testFetchersWithoutAppend() throws IOException {
		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {"IP", "fetcher1", "mega long fetcher 2"});
		exporter.end();
		assertContains("IP");
		assertContains("fetcher1");		
		assertContains("mega long fetcher 2");		
	}

	@Test
	public void testFetchersWithAppend() throws IOException {
		exporter.setAppend(true);
		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {"IP", "fetcher1", "mega long fetcher 2"});
		exporter.end();
		assertNotContains("IP");
		assertNotContains("fetcher1");		
		assertNotContains("mega long fetcher 2");		
	}
	
	@Test
	public void testClone() throws CloneNotSupportedException {
		Exporter exporter2 = (Exporter) exporter.clone();
		assertNotSame(exporter, exporter2);
	}
	
	@Test
	public void testNextAddressResultsWithNulls() throws IOException {
		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {"IP", "fetcher1", "mega long fetcher 2"});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost(), null, null});
		exporter.end();
	}

	protected void assertContains(String string, boolean contains) throws IOException {
		if (outputContent == null) {
			outputStream.close();
			// TODO: encoding???
			outputContent = new String(outputStream.toByteArray());
		}

		if (!((outputContent.indexOf(string) >= 0) ^ (!contains))) {
			throw new ComparisonFailure("Contains check failed", string, outputContent);
		}
	}

	protected void assertContains(String string) throws IOException {
		assertContains(string, true);
	}

	protected void assertNotContains(String string) throws IOException {
		assertContains(string, false);
	}
}
