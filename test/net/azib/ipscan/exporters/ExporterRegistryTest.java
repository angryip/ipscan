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
	
	public void testIterator() {
		for (Iterator i = ExporterRegistry.getInstance().iterator(); i.hasNext(); ) {
			Exporter exporter = (Exporter) i.next();
			assertNotNull(exporter);
			assertNotNull(exporter.getFilenameExtension());
		}
	}
	
	public void testCreate() {
		Exporter exporter;
		
		exporter = ExporterRegistry.getInstance().createExporter("aa.abc." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
		
		exporter = ExporterRegistry.getInstance().createExporter("/tmp/foo/megafile." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
	}

	public void testCreateFailes() {
		try {
			ExporterRegistry.getInstance().createExporter("noextension");
			fail();
		}
		catch (ExporterException e) {
			assertEquals("exporter.unknown", e.getMessage());
		}
		
		try {
			ExporterRegistry.getInstance().createExporter("unknown.extension");
			fail();
		}
		catch (ExporterException e) {
			assertEquals("exporter.unknown", e.getMessage());
		}
	}
	
}
