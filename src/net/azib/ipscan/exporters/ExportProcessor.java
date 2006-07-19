/**
 * 
 */
package net.azib.ipscan.exporters;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * Export Processor controls the actual exporting using the provided Exporter.
 *
 * @author anton
 */
public class ExportProcessor {

	private Exporter exporter;
	private String fileName;
	
	public ExportProcessor(Exporter exporter, String fileName) {
		this.exporter = exporter;
		this.fileName = fileName;
	}

	/**
	 * Called to execute the actual scanning process.
	 * @param scanningResults the scanning results, which are available
	 * @param feederInfo info about the Feeder configuration as String
	 * @param resultSelector optional (can be null) - determines results for saving or skipping
	 */
	public void process(ScanningResultList scanningResults, String feederInfo, ScanningResultSelector resultSelector) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName);
			
			exporter.start(outputStream, feederInfo);
	
			// set fetchers
			List fetchers = scanningResults.getFetchers();
			String[] fetcherNames = new String[fetchers.size()];
			int i = 0;
			for (Iterator j = fetchers.iterator(); j.hasNext(); i++) {
				fetcherNames[i] = Labels.getInstance().getString(((Fetcher)j.next()).getLabel());
			}			
			exporter.setFetchers(fetcherNames);

			int index = 0;
			for (Iterator j = scanningResults.iterator(); j.hasNext(); index++) {
				ScanningResult scanningResult = (ScanningResult) j.next();
				if (resultSelector == null || resultSelector.isResultSelected(index, scanningResult)) {
					exporter.nextAdressResults(scanningResult.getValues().toArray());
				}
			}
			
			exporter.end();
		}
		catch (Exception e) {
			throw new ExporterException("exporting failed", e);
		}
		finally {
			try {
				outputStream.close();
			}
			catch (Exception e) {}
		}
	}
	
	/**
	 * ScanningResultSelector can be implemented and passed to {@link ExportProcessor#process(ScanningResultList, String)}
	 */
	public static interface ScanningResultSelector {
		boolean isResultSelected(int index, ScanningResult result);
	}
}
