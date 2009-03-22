/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * Export Processor controls the actual exporting using the provided Exporter.
 *
 * @author Anton Keks
 */
public class ExportProcessor {

	private final Exporter exporter;
	private final File file;
	private final boolean append;
	
	public ExportProcessor(Exporter exporter, File file, boolean append) {
		this.exporter = exporter;
		this.file = file;
		this.append = append;
	}

	/**
	 * Called to execute the actual scanning process.
	 * @param scanningResults the scanning results, which are available
	 * @param filter optional (can be null) - determines results for saving or skipping
	 */
	public void process(ScanningResultList scanningResults, ScanningResultFilter filter) {
		FileOutputStream outputStream = null;
		try {
			if (append) {
				// let the exporter know
				exporter.shouldAppendTo(file);
			}
			outputStream = new FileOutputStream(file, append);
			
			exporter.start(outputStream, scanningResults.getFeederInfo());
	
			// set fetchers
			List<Fetcher> fetchers = scanningResults.getFetchers();
			String[] fetcherNames = new String[fetchers.size()];
			int i = 0;
			for (Fetcher fetcher : fetchers) {
				fetcherNames[i++] = fetcher.getName();
			}			
			exporter.setFetchers(fetcherNames);

			int index = 0;
			for (ScanningResult scanningResult : scanningResults) {
				if (filter == null || filter.isResultSelected(index, scanningResult)) {
					exporter.nextAdressResults(scanningResult.getValues().toArray());
				}
			}
			
			exporter.end();
		}
		catch (ExporterException e) {
			throw e;
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
	public static interface ScanningResultFilter {
		boolean isResultSelected(int index, ScanningResult result);
	}
}
