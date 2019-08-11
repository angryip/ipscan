package net.azib.ipscan.exporters;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.exporters.ExportProcessor.ScanningResultFilter;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportProcessorTest {
	private FetcherRegistry fetcherRegistry;
	
	@Before
	public void setUp() {
		fetcherRegistry = mock(FetcherRegistry.class);
		when(fetcherRegistry.getSelectedFetchers()).thenReturn(singletonList(new IPFetcher()));
	}

	@Test
	public void testProcess() throws Exception {
		File file = File.createTempFile("exportTest", "txt");
		ExportProcessor exportProcessor = new ExportProcessor(new TXTExporter(), file, false);
		
		ScanningResultList scanningResultList = new ScanningResultList(fetcherRegistry);
		scanningResultList.initNewScan(mockFeeder("megaFeeder"));
		scanningResultList.registerAtIndex(0, scanningResultList.createResult(InetAddress.getByName("192.168.0.13")));
		exportProcessor.process(scanningResultList, null);
		
		String content = readFileContent(file);
		
		assertTrue(content.contains("megaFeeder"));
		assertTrue(content.contains(new IPFetcher().getName()));
		assertFalse(content.contains("fooBar"));
		assertTrue(content.contains("192.168.0.13"));
	}
	
	@Test
	public void testProcessWithFilter() throws Exception {
		File file = File.createTempFile("exportTest", "txt");
		ExportProcessor exportProcessor = new ExportProcessor(new TXTExporter(), file, false);
		
		ScanningResultList scanningResultList = new ScanningResultList(fetcherRegistry);
		scanningResultList.initNewScan(mockFeeder("feeder2"));
		
		scanningResultList.registerAtIndex(0, scanningResultList.createResult(InetAddress.getByName("192.168.13.66")));
		scanningResultList.registerAtIndex(1, scanningResultList.createResult(InetAddress.getByName("192.168.13.67")));
		scanningResultList.registerAtIndex(2, scanningResultList.createResult(InetAddress.getByName("192.168.13.76")));
		
		exportProcessor.process(scanningResultList, new ScanningResultFilter() {
			public boolean apply(int index, ScanningResult result) {
				// select only IP addresses ending with 6
				return ((String)result.getValues().get(0)).endsWith("6");
			}
		});
		
		String content = readFileContent(file);
		
		assertTrue(content.contains("feeder2"));
		assertTrue(content.contains("192.168.13.66"));
		assertFalse(content.contains("192.168.13.67"));
		assertTrue(content.contains("192.168.13.76"));
	}
	
	private Feeder mockFeeder(String feederInfo) {
		Feeder feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn(feederInfo);
		when(feeder.getName()).thenReturn("feeder.range");
		return feeder;
	}

	private String readFileContent(File file) throws IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
}
