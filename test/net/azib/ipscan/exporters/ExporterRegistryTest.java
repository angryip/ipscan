/*
 * 
 */
package net.azib.ipscan.exporters;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * ExporterRegistryTest
 *
 * @author anton
 */
public class ExporterRegistryTest extends TestCase {
	
	private ExporterRegistry exporterRegistry = new ExporterRegistry(new Exporter[] {new TXTExporter(), new CSVExporter()});
	
	public void testIterator() {
		for (Iterator i = exporterRegistry.iterator(); i.hasNext(); ) {
			Exporter exporter = (Exporter) i.next();
			assertNotNull(exporter);
			assertNotNull(exporter.getFilenameExtension());
		}
	}
	
	public void testCreate() {
		Exporter exporter;
		
		exporter = exporterRegistry.createExporter("aa.abc." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
		
		exporter = exporterRegistry.createExporter("/tmp/foo/megafile." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
	}

	public void testCreateFailes() {
		try {
			exporterRegistry.createExporter("noextension");
			fail();
		}
		catch (ExporterException e) {
			assertEquals("exporter.unknown", e.getMessage());
		}
		
		try {
			exporterRegistry.createExporter("unknown.extension");
			fail();
		}
		catch (ExporterException e) {
			assertEquals("exporter.unknown", e.getMessage());
		}
	}
	
}
