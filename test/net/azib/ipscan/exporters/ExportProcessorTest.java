/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.exporters.ExportProcessor.ScanningResultSelector;
import net.azib.ipscan.fetchers.IPFetcher;

import junit.framework.TestCase;

/**
 * ExportProcessorTest
 *
 * @author anton
 */
public class ExportProcessorTest extends TestCase {

	public void testProcess() throws Exception {
		File file = File.createTempFile("exportTest", "txt");
		ExportProcessor exportProcessor = new ExportProcessor(new TXTExporter(), file.getAbsolutePath());
		
		ScanningResultList scanningResultList = new ScanningResultList();
		scanningResultList.setFetchers(Collections.singletonList(new IPFetcher()));
		scanningResultList.add("fooBar");
		scanningResultList.update(0, new ScanningResult(Collections.singletonList("super-IP-value"), ScanningSubject.RESULT_TYPE_UNKNOWN));
		exportProcessor.process(scanningResultList, "megaFeeder", null);
		
		String content = readFileContent(file);
		
		assertTrue(content.indexOf("megaFeeder") > 0);
		assertTrue(content.indexOf(Labels.getInstance().getString(new IPFetcher().getLabel())) > 0);
		assertTrue(content.indexOf("fooBar") < 0);		
		assertTrue(content.indexOf("super-IP-value") > 0);		
	}

	/**
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
	
	public void testProcessWithSelector() throws Exception {
		File file = File.createTempFile("exportTest", "txt");
		ExportProcessor exportProcessor = new ExportProcessor(new TXTExporter(), file.getAbsolutePath());
		
		ScanningResultList scanningResultList = new ScanningResultList();
		scanningResultList.setFetchers(Collections.singletonList(new IPFetcher()));
		
		scanningResultList.add("fooBar");
		scanningResultList.update(0, new ScanningResult(Collections.singletonList("fooBar"), ScanningSubject.RESULT_TYPE_UNKNOWN));
		scanningResultList.add("barFoo");
		scanningResultList.update(1, new ScanningResult(Collections.singletonList("barFoo"), ScanningSubject.RESULT_TYPE_UNKNOWN));
		scanningResultList.add("barBar");
		scanningResultList.update(2, new ScanningResult(Collections.singletonList("barBar"), ScanningSubject.RESULT_TYPE_UNKNOWN));
		
		exportProcessor.process(scanningResultList, "feeder2", new ScanningResultSelector() {
			public boolean isResultSelected(int index, ScanningResult result) {
				return ((String)result.getValues().get(0)).startsWith("bar");
			}
		});
		
		String content = readFileContent(file);
		
		assertTrue(content.indexOf("feeder2") > 0);
		assertFalse(content.indexOf("fooBar") > 0);		
		assertTrue(content.indexOf("barFoo") > 0);		
		assertTrue(content.indexOf("barBar") > 0);		
	}

}
