/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;

import java.io.File;
import java.io.FileOutputStream;

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
			var fetchers = scanningResults.getFetchers();
			var fetcherNames = new String[fetchers.size()];
			var i = 0;
			for (var fetcher : fetchers) {
				fetcherNames[i++] = fetcher.getName();
			}			
			exporter.setFetchers(fetcherNames);

			var index = 0;
			for (var scanningResult : scanningResults) {
				if (filter == null || filter.apply(index++, scanningResult)) {
					exporter.nextAddressResults(scanningResult.getValues().toArray());
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
				catch (Exception ignore) {}
			}
		}
	}
	
	/**
	 * ScanningResultSelector can be implemented and passed to {@link ExportProcessor#process(ScanningResultList, ScanningResultFilter)}
	 */
	public interface ScanningResultFilter {
		boolean apply(int index, ScanningResult result);
	}
}
