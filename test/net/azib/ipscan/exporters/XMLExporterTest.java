/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;

/**
 * XML Exporter Test
 *
 * @author anton
 */
public class XMLExporterTest extends AbstractExporterTestCase {

	protected void setUp() throws Exception {
		Labels.initialize(Locale.ENGLISH);
		super.setUp();
	}

	protected Exporter createExporter() {
		return new XMLExporter();
	}
	
	public void testHeaderWithoutAppend() throws IOException {
		exporter.start(outputStream, "feederstuff");
		exporter.end();
		assertContains(Version.FULL_NAME);
		assertContains(Version.WEBSITE);
	}

	public void testFetchersWithoutAppend() throws IOException {
		exporter.start(outputStream, "feederstuff");
		exporter.setFetchers(new String[] {"fetcher1", Labels.getInstance().getString("fetcher.IP"), "mega long fetcher 2"});
		exporter.nextAdressResults(new Object[] {"", "123", ""});
		exporter.end();
		assertContains("IP");
		assertContains("address=\"123\"");
		assertContains("fetcher1");		
		assertContains("mega long fetcher 2");		
	}

	public void testFetchersWithAppend() {
		try {
			exporter.setAppend(true);
			fail();
		}
		catch (ExporterException e) {
			assertEquals("xml.noAppend", e.getMessage());
		}
	}

	public void testFeederInfoWithName() throws IOException {
		exporter.start(outputStream, "Blah: 192.168.1.1 - 192.168.3.255");
		exporter.end();
		assertContains("name=\"Blah\"");
		assertContains("192.168.1.1 - 192.168.3.255");
	}

	public void testFeederInfoNoName() throws IOException {
		exporter.start(outputStream, "Booga 192.168.1.1/123");
		exporter.end();
		assertContains("Booga 192.168.1.1/123");
	}
	
	public void testValidXML() throws Exception {
		exporter.start(outputStream, "<megaInfo'''");
		exporter.setFetchers(new String[] {"IP", "hello", "fet::cher2"});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost().getHostAddress(), "w?:orld'", new Integer(53)});
		exporter.nextAdressResults(new Object[] {InetAddress.getLocalHost().getHostAddress(), "bug>>a", new Integer(-1)});
		exporter.end();
		assertContains("<megaInfo'''");
		
		DocumentBuilder documentBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
		documentBuilder.parse(new ByteArrayInputStream(outputContent.getBytes(XMLExporter.ENCODING)));
	}
	
}
