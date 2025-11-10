/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResult.ResultType;
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
 * @author Anton Keks
 */
public class GotoMenuActions {

	static class NextHost implements Listener {

		final ResultTable resultTable;
		final ResultType whatToSearchFor;
		
		NextHost(ResultTable resultTable, ResultType whatToSearchFor) {
			this.resultTable = resultTable;
			this.whatToSearchFor = whatToSearchFor;
		}
		
		protected int inc(int i) {
			return i+1;
		}
		
		protected int startIndex() {
			return resultTable.getSelectionIndex();
		}

		public final void handleEvent(Event event) {
			var results = resultTable.getScanningResults();

			var numElements = resultTable.getItemCount();
			var startIndex = startIndex();
			
			for (var i = inc(startIndex); i < numElements && i >= 0; i = inc(i)) {
				var scanningResult = results.getResult(i);
				
				if (whatToSearchFor.matches(scanningResult.getType())) {
					resultTable.setSelection(i);
					resultTable.setFocus();
					return;
				}
				
			}
						
			// rewind
			if (startIndex >= 0 && startIndex < numElements) {
				resultTable.deselectAll();
				handleEvent(event);
			}
		}

	}
	
	static class PrevHost extends NextHost {

		public PrevHost(ResultTable resultTable, ResultType whatToSearchFor) {
			super(resultTable, whatToSearchFor);
		}

		protected int inc(int i) {
			return i-1;
		}

		protected int startIndex() {
			var curIndex = resultTable.getSelectionIndex();
			return curIndex >= 0 ? curIndex : resultTable.getItemCount();
		}
	}
	
	public static final class NextAliveHost extends NextHost {
		public NextAliveHost(ResultTable resultTable) {
			super(resultTable, ResultType.ALIVE);
		}
	}
	
	public static final class NextDeadHost extends NextHost {
		public NextDeadHost(ResultTable resultTable) {
			super(resultTable, ResultType.DEAD);
		}
	}
	
	public static final class NextHostWithInfo extends NextHost {
		public NextHostWithInfo(ResultTable resultTable) {
			super(resultTable, ResultType.WITH_PORTS);
		}
	}
	
	public static final class PrevAliveHost extends PrevHost {
		public PrevAliveHost(ResultTable resultTable) {
			super(resultTable, ResultType.ALIVE);
		}
	}
	
	public static final class PrevDeadHost extends PrevHost {
		public PrevDeadHost(ResultTable resultTable) {
			super(resultTable, ResultType.DEAD);
		}
	}
	
	public static final class PrevHostWithInfo extends PrevHost {
		public PrevHostWithInfo(ResultTable resultTable) {
			super(resultTable, ResultType.WITH_PORTS);
		}
	}
	
	public static final class Find implements Listener {

		private final ResultTable resultTable;
		private final StatusBar statusBar;
		private String lastText = "";

		public Find(StatusBar statusBar, ResultTable resultTable) {
			this.statusBar = statusBar;
			this.resultTable = resultTable;
		}
		
		public void handleEvent(Event event) {
			var dialog = new InputDialog(Labels.getLabel("title.find"), Labels.getLabel("text.find"));
			var text = dialog.open(lastText, Labels.getLabel("button.find.next"));
			if (text == null) {
				return;
			}
			lastText = text;
			
			try {
				statusBar.setStatusText(Labels.getLabel("state.searching"));
				findText(text, statusBar.getShell());
			}
			finally {
				statusBar.setStatusText(null);				
			}
		}

		private void findText(String text, Shell activeShell) {
			var results = resultTable.getScanningResults();

			var startIndex = resultTable.getSelectionIndex() + 1;

			var foundIndex = results.findText(text, startIndex);
			
			if (foundIndex >= 0) {
				// if found, then select and finish
				resultTable.setSelection(foundIndex);
				resultTable.setFocus();
				return;
			}
			
			if (startIndex > 0) {
				// if started not from the beginning, offer to restart				
				var messageBox = new MessageBox(activeShell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				messageBox.setText(Labels.getLabel("title.find"));
				messageBox.setMessage(Labels.getLabel("text.find.notFound") + "\n" + Labels.getLabel("text.find.restart"));
				if (messageBox.open() == SWT.YES) {
					resultTable.deselectAll();
					findText(text, activeShell);
				}
			}
			else {
				// searching is finished, nothing was found				
				var messageBox = new MessageBox(activeShell, SWT.OK | SWT.ICON_INFORMATION);
				messageBox.setText(Labels.getLabel("title.find"));
				messageBox.setMessage(Labels.getLabel("text.find.notFound"));
				messageBox.open();
			}
		}
	}	
	
}
