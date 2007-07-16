/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import java.io.FileOutputStream;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * Export Processor controls the actual exporting using the provided Exporter.
 *
 * @author Anton Keks
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
	 * @param resultSelector optional (can be null) - determines results for saving or skipping
	 */
	public void process(ScanningResultList scanningResults, ScanningResultSelector resultSelector) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileName);
			
			exporter.start(outputStream, scanningResults.getFeederInfo());
	
			// set fetchers
			List<Fetcher> fetchers = scanningResults.getFetchers();
			String[] fetcherNames = new String[fetchers.size()];
			int i = 0;
			for (Fetcher fetcher : fetchers) {
				fetcherNames[i] = Labels.getLabel(fetcher.getLabel());
			}			
			exporter.setFetchers(fetcherNames);

			int index = 0;
			for (ScanningResult scanningResult : scanningResults) {
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
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (Exception e) {}
			}
		}
	}
	
	/**
	 * ScanningResultSelector can be implemented and passed to {@link ExportProcessor#process(ScanningResultList, String)}
	 */
	public static interface ScanningResultSelector {
		boolean isResultSelected(int index, ScanningResult result);
	}
}
