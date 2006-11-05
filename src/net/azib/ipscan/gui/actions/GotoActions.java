/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.gui.InputDialog;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * GotoActions
 *
 * @author anton
 */
public class GotoActions {

	private static class NextHost implements Listener {

		private ResultTable resultTable;
		private int whatToSearchFor;
		
		NextHost(ResultTable resultTable, int whatToSearchFor) {
			this.resultTable = resultTable;
			this.whatToSearchFor = whatToSearchFor;
		}

		public void handleEvent(Event event) {
			ScanningResultList results = resultTable.getScanningResults();
			
			int numElements = resultTable.getItemCount();
			int startElement = resultTable.getSelectionIndex() + 1;
			
			for (int i = startElement; i < numElements; i++) {
				ScanningResult scanningResult = results.getResult(i);
				
				if (scanningResult.getType() >= whatToSearchFor) {
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
	
	public static class NextAliveHost extends NextHost {
		public NextAliveHost(ResultTable resultTable) {
			super(resultTable, ScanningSubject.RESULT_TYPE_ALIVE);
		}
	}
	
	public static class NextDeadHost extends NextHost {
		public NextDeadHost(ResultTable resultTable) {
			super(resultTable, ScanningSubject.RESULT_TYPE_DEAD);
		}
	}
	
	public static class NextHostWithInfo extends NextHost {
		public NextHostWithInfo(ResultTable resultTable) {
			super(resultTable, ScanningSubject.RESULT_TYPE_ADDITIONAL_INFO);
		}
	}
	
	public static class Find implements Listener {

		private ResultTable resultTable;
		private StatusBar statusBar;
		private String lastText = "";
		
		public Find(StatusBar statusBar, ResultTable resultTable) {
			this.statusBar = statusBar;
			this.resultTable = resultTable;
		}
		
		public void handleEvent(Event event) {
			InputDialog dialog = new InputDialog(Labels.getLabel("title.find"), Labels.getLabel("text.find"));
			String text = dialog.open(lastText);
			if (text == null) {
				return;
			}
			lastText = text;
			
			try {
				statusBar.setStatusText(Labels.getLabel("state.searching"));

				findText(text, event.display.getActiveShell());
			}
			finally {
				statusBar.setStatusText(null);				
			}
		}

		private void findText(String text, Shell activeShell) {
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
				MessageBox messageBox = new MessageBox(activeShell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				messageBox.setText(Labels.getLabel("title.find"));
				messageBox.setMessage(Labels.getLabel("text.find.notFound") + " " + Labels.getLabel("text.find.restart"));
				if (messageBox.open() == SWT.YES) {
					resultTable.deselectAll();
					findText(text, activeShell);
				}
			}
			else {
				MessageBox messageBox = new MessageBox(activeShell, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText(Labels.getLabel("title.find"));
				messageBox.setMessage(Labels.getLabel("text.find.notFound"));
				messageBox.open();
			}
		}
	}	
	
}
