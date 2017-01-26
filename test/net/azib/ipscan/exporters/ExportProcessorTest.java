package net.azib.ipscan.exporters;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.exporters.ExportProcessor.ScanningResultFilter;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ExportProcessorTest
 *
 * @author Anton Keks
 */
public class ExportProcessorTest {
	
	private FetcherRegistry fetcherRegistry;
	
	@Before
	public void setUp() {
		fetcherRegistry = mock(FetcherRegistry.class);
		when(fetcherRegistry.getSelectedFetchers())
			.thenReturn(Collections.<Fetcher>singletonList(new IPFetcher()));
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
		
		assertTrue(content.indexOf("megaFeeder") > 0);
		assertTrue(content.indexOf(new IPFetcher().getName()) > 0);
		assertTrue(content.indexOf("fooBar") < 0);		
		assertTrue(content.indexOf("192.168.0.13") > 0);		
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
		
		assertTrue(content.indexOf("feeder2") > 0);
		assertTrue(content.indexOf("192.168.13.66") > 0);
		assertTrue(content.indexOf("192.168.13.67") < 0);		
		assertTrue(content.indexOf("192.168.13.76") > 0);		
	}
	
	private Feeder mockFeeder(String feederInfo) {
		Feeder feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn(feederInfo);
		when(feeder.getName()).thenReturn("feeder.range");
		return feeder;
	}

	private String readFileContent(File file) throws FileNotFoundException, IOException {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
}
