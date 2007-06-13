/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.exporters.ExportProcessor;
import net.azib.ipscan.exporters.Exporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.exporters.ExportProcessor.ScanningResultSelector;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.UserErrorException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

/**
 * FileActions
 * 
 * @author anton
 */
public class FileActions {

	public static class Exit implements Listener {
		public void handleEvent(Event event) {
			event.display.getActiveShell().close();
		}
	}

	private static class SaveResults implements Listener {
		private ExporterRegistry exporterRegistry;
		private ResultTable resultTable;
		private StatusBar statusBar;
		private boolean isSelection;
		
		private SaveResults(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar, boolean isSelection) {
			this.exporterRegistry = exporterRegistry;
			this.resultTable = resultTable;
			this.statusBar = statusBar;
			// TODO: implement isSelection
			this.isSelection = isSelection;
		}

		public void handleEvent(Event event) {
			if (resultTable.getItemCount() <= 0) {
				throw new UserErrorException("commands.noResults");
			}
			
			// create the file dialog
			FileDialog fileDialog = new FileDialog(resultTable.getShell(), SWT.SAVE);

			// gather lists of extensions and exporter names
			List extensions = new ArrayList();
			List descriptions = new ArrayList();
			StringBuffer labelBuffer = new StringBuffer(Labels.getLabel(isSelection ? "title.saveSelection" : "title.saveAll"));
			addFileExtensions(extensions, descriptions, labelBuffer);
			
			// initialize other stuff
			fileDialog.setText(labelBuffer.toString());
			fileDialog.setFilterExtensions((String[]) extensions.toArray(new String[extensions.size()]));
			fileDialog.setFilterNames((String[]) descriptions.toArray(new String[descriptions.size()]));
			
			// show the dialog and receive the filename
			String fileName = fileDialog.open();

			// check the received file name
			if (fileName != null) {
				// create exporter instance
				Exporter exporter = exporterRegistry.createExporter(fileName);
				
				statusBar.setStatusText(Labels.getLabel("state.saving"));
				
				ExportProcessor exportProcessor = new ExportProcessor(exporter, fileName);
				
				// in case of isSelection we need to create our ScanningResultSelector
				ScanningResultSelector scanningResultSelector = null;
				if (isSelection) {
					scanningResultSelector = new ScanningResultSelector() {
						public boolean isResultSelected(int index, ScanningResult result) {
							return resultTable.isSelected(index);
						}
					};
				}
				
				exportProcessor.process(resultTable.getScanningResults(), resultTable.getFeederInfo(), scanningResultSelector);
				
				statusBar.setStatusText(null);
			}
		}

		private void addFileExtensions(List extensions, List descriptions, StringBuffer sb) {
			sb.append(" (");
			for (Iterator i = exporterRegistry.iterator(); i.hasNext(); ) {
				Exporter exporter = (Exporter) i.next();
				extensions.add("*." + exporter.getFilenameExtension());
				sb.append(exporter.getFilenameExtension()).append(", ");;
				descriptions.add(Labels.getLabel(exporter.getLabel()));
			}
			// strip the last comma
			sb.delete(sb.length() - 2, sb.length());
			sb.append(")");
		}
	}
	
	public static class SaveAll extends SaveResults {
		public SaveAll(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar) {
			super(exporterRegistry, resultTable, statusBar, false);
		}
	}

	public static class SaveSelection extends SaveResults {
		public SaveSelection(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar) {
			super(exporterRegistry, resultTable, statusBar, true);
		}
	}
}
