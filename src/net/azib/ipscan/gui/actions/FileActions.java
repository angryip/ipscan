/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.ArrayList;
import java.util.List;

import net.azib.ipscan.Main;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.exporters.ExportProcessor;
import net.azib.ipscan.exporters.Exporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.exporters.ExportProcessor.ScanningResultSelector;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

/**
 * FileActions
 * 
 * @author Anton Keks
 */
public class FileActions {

	public static final class Exit implements Listener {
		public void handleEvent(Event event) {
			event.display.getActiveShell().close();
		}
	}

	static abstract class SaveResults implements Listener {
		private final ExporterRegistry exporterRegistry;
		private final ResultTable resultTable;
		private final StatusBar statusBar;
		private final boolean isSelection;
		private final StateMachine stateMachine;
		
		SaveResults(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar, StateMachine stateMachine, boolean isSelection) {
			this.exporterRegistry = exporterRegistry;
			this.resultTable = resultTable;
			this.statusBar = statusBar;
			this.stateMachine = stateMachine;
			this.isSelection = isSelection;
		}

		public void handleEvent(Event event) {
			if (resultTable.getItemCount() <= 0) {
				throw new UserErrorException("commands.noResults");
			}
			
			if (!stateMachine.inState(ScanningState.IDLE)) {
				MessageBox box = new MessageBox(resultTable.getShell(), SWT.YES | SWT.NO | SWT.ICON_WARNING);
				box.setText(Version.NAME);
				box.setMessage(Labels.getLabel("exception.ExporterException.scanningInProgress"));
				if (box.open() != SWT.YES)
					return;
			}
			
			// create the file dialog
			FileDialog fileDialog = new FileDialog(resultTable.getShell(), SWT.SAVE);

			// gather lists of extensions and exporter names
			List<String> extensions = new ArrayList<String>();
			List<String> descriptions = new ArrayList<String>();
			StringBuffer labelBuffer = new StringBuffer(Labels.getLabel(isSelection ? "title.exportSelection" : "title.exportAll"));
			addFileExtensions(extensions, descriptions, labelBuffer);
			
			// initialize other stuff
			fileDialog.setText(labelBuffer.toString());
			fileDialog.setFilterExtensions(extensions.toArray(new String[extensions.size()]));
			fileDialog.setFilterNames(descriptions.toArray(new String[descriptions.size()]));
			
			// show the dialog and receive the filename
			String fileName = fileDialog.open();

			// check the received file name
			if (fileName != null) {
				// create exporter instance
				Exporter exporter = exporterRegistry.createExporter(fileName);
				
				statusBar.setStatusText(Labels.getLabel("state.exporting"));
				
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
				
				exportProcessor.process(resultTable.getScanningResults(), scanningResultSelector);
				
				statusBar.setStatusText(null);
			}
		}

		private final void addFileExtensions(List<String> extensions, List<String> descriptions, StringBuffer sb) {
			sb.append(" (");
			for (Exporter exporter : exporterRegistry) {
				extensions.add("*." + exporter.getFilenameExtension());
				sb.append(exporter.getFilenameExtension()).append(", ");
				descriptions.add(Labels.getLabel(exporter.getId()));
			}
			// strip the last comma
			sb.delete(sb.length() - 2, sb.length());
			sb.append(")");
		}
	}
	
	public static final class SaveAll extends SaveResults {
		public SaveAll(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar, StateMachine stateMachine) {
			super(exporterRegistry, resultTable, statusBar, stateMachine, false);
		}
	}

	public static final class SaveSelection extends SaveResults {
		public SaveSelection(ExporterRegistry exporterRegistry, ResultTable resultTable, StatusBar statusBar, StateMachine stateMachine) {
			super(exporterRegistry, resultTable, statusBar, stateMachine, true);
		}
	}
	
	public static final class NewWindow implements Listener {
		public void handleEvent(Event event) {
			// start another instance in a new thread
			// doesn't currently work...
			new Thread("main") {
				public void run() {					
					Main.main();
				}
			}.start();
		}
	}

}
