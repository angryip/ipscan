/*
 * 
 */
package net.azib.ipscan.exporters;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * ExporterRegistryTest
 *
 * @author Anton Keks
 */
public class ExporterRegistryTest {
	
	private ExporterRegistry exporterRegistry = new ExporterRegistry(Arrays.asList(new TXTExporter(), new CSVExporter()));
	
	@Test
	public void testIterator() {
		for (Exporter exporter : exporterRegistry) {
			assertNotNull(exporter);
			assertNotNull(exporter.getFilenameExtension());
		}
	}
	
	@Test
	public void testCreate() {
		Exporter exporter;
		
		exporter = exporterRegistry.createExporter("aa.abc." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
		
		exporter = exporterRegistry.createExporter("/tmp/foo/megafile." + new TXTExporter().getFilenameExtension());
		assertTrue(exporter instanceof TXTExporter);
	}

	@Test
	public void testCreateFailed() {
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
