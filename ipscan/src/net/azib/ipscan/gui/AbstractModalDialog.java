/**
 * 
 */
package net.azib.ipscan.gui;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * This is the base of a modal dialog window
 *
 * @author anton
 */
public abstract class AbstractModalDialog {

	protected Shell shell = null;

	public void open() {
		// center dialog box according to the parent window
		Rectangle parentBounds = shell.getParent().getBounds();
		Rectangle childBounds = shell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		shell.setLocation(x, y);
		
		// open the dialog box 
		shell.open();
		
		// create a separate event loop
		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		
		// destroy the window
		shell.dispose();
	}
	
	// common listeners follow
	
	protected static class UpButtonListener implements Listener {
		
		private List list;

		public UpButtonListener(List list) {
			this.list = list;
		}

		public void handleEvent(Event event) {
			if (list.isSelected(0)) {
				// do not move anything if the first item is selected
				return;
			}
			
			int[] selectedItems = list.getSelectionIndices();
			for (int i = 0; i < selectedItems.length; i++) {
				// here, index is always > 0
				int index = selectedItems[i];

				list.deselect(index);
				String oldItem = list.getItem(index - 1);
				list.setItem(index - 1, list.getItem(index));
				list.setItem(index, oldItem);
				list.select(index - 1);
			}
			
			list.setTopIndex(selectedItems[0] - 2);
		}
	}

	protected static class DownButtonListener implements Listener {
		
		private List list;

		public DownButtonListener(List list) {
			this.list = list;
		}

		public void handleEvent(Event event) {
			if (list.isSelected(list.getItemCount() - 1)) {
				// do not move anything if the last items is selected
				return;
			}
			
			int[] selectedItems = list.getSelectionIndices();
			for (int i = selectedItems.length - 1; i >= 0; i--) {
				// here, index is always < getItemCount()
				int index = selectedItems[i];

				list.deselect(index);
				String oldItem = list.getItem(index + 1);
				list.setItem(index + 1, list.getItem(index));
				list.setItem(index, oldItem);
				list.select(index + 1);
			}
			
			list.setTopIndex(selectedItems[0]);
		}
	}

}
