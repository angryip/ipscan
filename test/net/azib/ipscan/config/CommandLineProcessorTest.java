/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.exporters.Exporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.feeders.FeederCreator;
import net.azib.ipscan.feeders.FeederRegistry;

import org.junit.Before;
import org.junit.Test;

/**
 * CommandLineProcessorTest
 *
 * @author Anton Keks
 */
public class CommandLineProcessorTest {
	private CommandLineProcessor processor;
	private FeederCreator feederCreator;
	private ExporterRegistry exporters;
	
	@Before
	public void prepare() {
		feederCreator = mock(FeederCreator.class);
		exporters = mock(ExporterRegistry.class);
		processor = new CommandLineProcessor(new MockFeederRegistry(feederCreator), exporters);
	}
	
	@Test
	public void toStringGeneratesUsageHelp() throws Exception {
		when(feederCreator.getFeederId()).thenReturn("feeder.range");
		when(feederCreator.serializePartsLabels()).thenReturn(new String[] {"feeder.range.to"});
		Exporter exporter = mock(Exporter.class);
		when(exporters.iterator()).thenReturn(Collections.singleton(exporter).iterator());
		when(exporter.getFilenameExtension()).thenReturn("pdf");
		when(exporter.getId()).thenReturn("exporter.txt");

		String usage = processor.toString();
		assertTrue(usage.contains("-f:range"));
		assertTrue(usage.contains(Labels.getLabel("feeder.range.to")));
		assertTrue(usage.contains(".pdf"));
		assertTrue(usage.contains("-q"));
//		assertTrue(usage.contains("-a"));
		assertTrue(usage.contains("-s"));
	}

	@Test
	public void minimal() throws Exception {
		when(feederCreator.getFeederId()).thenReturn("feeder.feeder");
		when(feederCreator.serializePartsLabels()).thenReturn(new String[] {"1st", "2nd"});
		Exporter txtExporter = mock(Exporter.class);
		when(exporters.createExporter("file.txt")).thenReturn(txtExporter);

		processor.parse("-f:feeder", "arg1", "arg2", "-o", "file.txt");

		assertEquals(feederCreator, processor.feederCreator);
		assertEquals("file.txt", processor.outputFilename);
		assertEquals(txtExporter, processor.exporter);
		
		assertFalse(processor.autoQuit);
		assertFalse(processor.appendToFile);
		assertTrue("specifying exporter should enable autoStart", processor.autoStart);
		
        verify(feederCreator).unserialize(new String[] {"arg1", "arg2"});
	}

	@Test
	public void options() throws Exception {
		when(feederCreator.getFeederId()).thenReturn("feeder.mega");
		when(feederCreator.serializePartsLabels()).thenReturn(new String[0]);

		processor.parse("-s", "-f:mega", "-aq");
		
		assertEquals(feederCreator, processor.feederCreator);
		assertTrue(processor.autoQuit);
		assertTrue(processor.autoStart);
		assertTrue(processor.appendToFile);
		
		verify(feederCreator).unserialize(new String[0]);
	}

	@Test(expected=IllegalArgumentException.class)
	public void missingRequiredFeeder() throws Exception {
		processor.parse("-o", "exporter");
	}

	@Test(expected=IllegalArgumentException.class)
	public void inexistentExporter() throws Exception {
		processor.parse("-o", "blah");
	}

	@Test(expected=IllegalArgumentException.class)
	public void inexistentFeeder() throws Exception {
		processor.parse("-f:blah");
	}

	@Test(expected=IllegalArgumentException.class)
	public void extraExporter() throws Exception {
		processor.parse("-f:feeder", "-o", "exporter.txt", "-o", "exporter.xml");
	}

	@Test(expected=IllegalArgumentException.class)
	public void extraFeeder() throws Exception {
		processor.parse("-f:feeder", "-o", "exporter.xml", "-f:feeder");
	}
	
	public static class MockFeederRegistry implements FeederRegistry<FeederCreator> {
		
		private List<FeederCreator> list;
		
		public MockFeederRegistry(FeederCreator ... creators) {
			list = Arrays.asList(creators);
		}

		public void select(String feederId) {
		}

		public Iterator<FeederCreator> iterator() {
			return list.iterator();
		}

	}
}
