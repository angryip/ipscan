/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.gui.InputDialog;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ResultTable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

/**
 * GotoActions
 *
 * @author anton
 */
public class GotoActions {

	public static class NextHost implements Listener {
		
		private MainWindow mainWindow;
		private int whatToSearchFor;
		
		public NextHost(MainWindow mainWindow, int whatToSearchFor) {
			this.mainWindow = mainWindow;
			this.whatToSearchFor = whatToSearchFor;
		}

		public void handleEvent(Event event) {
			ResultTable resultTable = mainWindow.getResultTable();
			ScanningResultList results = resultTable.getScanningResults();
			
			int numElements = resultTable.getItemCount();
			int startElement = resultTable.getSelectionIndex() + 1;
			
			for (int i = startElement; i < numElements; i++) {
				ScanningResult scanningResult = results.getResult(i);
				
				if (scanningResult.getType() == whatToSearchFor) {
					resultTable.setSelection(i);
					resultTable.setFocus();
					return;
				}
				
			}
						
			if (startElement > 0) {
				resultTable.deselectAll();
				handleEvent(event);
			}
		}

	}
	
	public static class Find implements Listener {
		
		private MainWindow mainWindow;
		private String lastText = "";
		
		public Find(MainWindow mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		public void handleEvent(Event event) {
			
			InputDialog dialog = new InputDialog(Labels.getInstance().getString("title.find"), Labels.getInstance().getString("text.find"));
			dialog.setText(lastText);
			String text = dialog.open();
			if (text == null) {
				return;
			}
			lastText = text;
			
			try {
				mainWindow.setStatusText(Labels.getInstance().getString("state.searching"));

				findText(text);
			}
			finally {
				mainWindow.setStatusText(null);				
			}
		}

		private void findText(String text) {
			
			ResultTable resultTable = mainWindow.getResultTable();
			ScanningResultList results = resultTable.getScanningResults();
			
			int numElements = resultTable.getItemCount();
			int startElement = resultTable.getSelectionIndex() + 1;
			
			for (int i = startElement; i < numElements; i++) {
				ScanningResult scanningResult = results.getResult(i);
				
				List values = scanningResult.getValues();
				
				for (Iterator j = values.iterator(); j.hasNext();) {
					String value = (String) j.next();
					
					// TODO: case-insensitive search
					if (value != null && value.indexOf(text) >= 0) {
						resultTable.setSelection(i);
						resultTable.setFocus();
						return;
					}
				}
				
			}
			
			if (startElement > 0) {
				MessageBox messageBox = new MessageBox(mainWindow.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				messageBox.setText(Labels.getInstance().getString("title.find"));
				messageBox.setMessage(Labels.getInstance().getString("text.find.notFound") + " " + Labels.getInstance().getString("text.find.restart"));
				if (messageBox.open() == SWT.YES) {
					resultTable.deselectAll();
					findText(text);
				}
			}
			else {
				MessageBox messageBox = new MessageBox(mainWindow.getShell(), SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText(Labels.getInstance().getString("title.find"));
				messageBox.setMessage(Labels.getInstance().getString("text.find.notFound"));
				messageBox.open();
			}
			
		}
		
	}	
	
	
}
