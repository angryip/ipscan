/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

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
		feederCreator = createMock(FeederCreator.class);
		exporters = createMock(ExporterRegistry.class);
		processor = new CommandLineProcessor(new MockFeederRegistry(feederCreator), exporters);
	}
	
	@Test
	public void toStringGeneratesUsageHelp() throws Exception {
		expect(feederCreator.getFeederId()).andReturn("feeder.range");
		expect(feederCreator.serializePartsLabels()).andReturn(new String[] {"feeder.range.to"});
		Exporter exporter = createMock(Exporter.class);
		expect(exporters.iterator()).andReturn(Collections.singleton(exporter).iterator());
		expect(exporter.getFilenameExtension()).andReturn("pdf");
		expect(exporter.getId()).andReturn("exporter.txt");
		replay(feederCreator, exporters, exporter);
		
		String usage = processor.toString();
		assertTrue(usage.contains("-f:range"));
		assertTrue(usage.contains(Labels.getLabel("feeder.range.to")));
		assertTrue(usage.contains(".pdf"));
		assertTrue(usage.contains("-q"));
//		assertTrue(usage.contains("-a"));
		assertTrue(usage.contains("-s"));
		
		verify(feederCreator, exporters, exporter);
	}

	@Test
	public void minimal() throws Exception {
		expect(feederCreator.getFeederId()).andReturn("feeder.feeder");
		expect(feederCreator.serializePartsLabels()).andReturn(new String[] {"1st", "2nd"});
		feederCreator.unserialize(aryEq(new String[] {"arg1", "arg2"})); expectLastCall();
		Exporter txtExporter = createMock(Exporter.class);
		expect(exporters.createExporter("file.txt")).andReturn(txtExporter);
		replay(feederCreator, exporters);
		
		processor.parse("-f:feeder", "arg1", "arg2", "-o", "file.txt");

		assertEquals(feederCreator, processor.feederCreator);
		assertEquals("file.txt", processor.outputFilename);
		assertEquals(txtExporter, processor.exporter);
		
		assertFalse(processor.autoQuit);
		assertFalse(processor.appendToFile);
		assertTrue("specifying exporter should enable autoStart", processor.autoStart);
		
		verify(feederCreator, exporters);
	}

	@Test
	public void options() throws Exception {
		expect(feederCreator.getFeederId()).andReturn("feeder.mega");
		expect(feederCreator.serializePartsLabels()).andReturn(new String[0]);
		feederCreator.unserialize(aryEq(new String[0]));
		replay(feederCreator);

		processor.parse("-s", "-f:mega", "-aq");
		
		assertEquals(feederCreator, processor.feederCreator);
		assertTrue(processor.autoQuit);
		assertTrue(processor.autoStart);
		assertTrue(processor.appendToFile);
		
		verify(feederCreator);
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
