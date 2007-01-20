/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
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
		if (shell.getParent() != null) {
			Rectangle parentBounds = shell.getParent().getBounds();
			Rectangle childBounds = shell.getBounds();
			int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
			int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
			shell.setLocation(x, y);
		}
		
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
	
	/**
	 * Positions 2 buttons at the bottom-right part of the shell.
	 * On MacOS also changes ok and cancel button order.
	 * @param okButton
	 * @param cancelButton can be null
	 */
	protected void positionButtons(Button okButton, Button cancelButton) {
		shell.setDefaultButton(okButton);
		Rectangle clientArea = shell.getClientArea();
		
		Point size = okButton.computeSize(85, SWT.DEFAULT);
		okButton.setSize(size);
		
		if (cancelButton != null) {
			cancelButton.setSize(size);
		
			if (Platform.MAC_OS) {
				// Mac OS users expect button order to be reverse
				Button fooButton = okButton;
				okButton = cancelButton;
				cancelButton = fooButton;
			}
			// both buttons
			cancelButton.setLocation(clientArea.width - size.x - 10, clientArea.height - size.y - 10);
			okButton.setLocation(clientArea.width - size.x * 2 - 20, clientArea.height - size.y - 10);	
		}
		else {
			// only one button
			okButton.setLocation(clientArea.width - size.x - 10, clientArea.height - size.y - 10);
		}
	}
	
	/**
	 * Positions 2 buttons at the bottom-right part of the shell in the FormLayout.
	 * On MacOS also changes ok and cancel button order.
	 * @param okButton
	 * @param cancelButton 
	 * @param control the bottom-right widget, used as a guide
	 */
	protected void positionButtonsInFormLayout(Button okButton, Button cancelButton, Control control) {
		shell.setDefaultButton(okButton);
		
		if (Platform.MAC_OS) {
			// Mac OS users expect button order to be reverse
			Button fooButton = okButton;
			okButton = cancelButton;
			cancelButton = fooButton;
		}
		// both buttons
		cancelButton.setLayoutData(LayoutHelper.formData(85, SWT.DEFAULT, null, new FormAttachment(control, 0, SWT.RIGHT), new FormAttachment(control, 8), null));
		okButton.setLayoutData(LayoutHelper.formData(85, SWT.DEFAULT, null, new FormAttachment(cancelButton, -10), new FormAttachment(control, 8), null));
	}

	// common listeners follow
	
	protected static class UpButtonListener implements Listener {
		
		private List list;

		public UpButtonListener(List list) {
			this.list = list;
		}

		public void handleEvent(Event event) {
			if (list.getSelectionCount() == 0 || list.isSelected(0)) {
				// do not move anything if either nothing is selected or only the first item is selected
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
			
			if (!Platform.MAC_OS) {
				// this doesn't look good on Mac
				list.setTopIndex(selectedItems[0] - 2);	
			}
		}
	}

	protected static class DownButtonListener implements Listener {
		
		private List list;

		public DownButtonListener(List list) {
			this.list = list;
		}

		public void handleEvent(Event event) {
			if (list.getSelectionCount() == 0 || list.isSelected(list.getItemCount() - 1)) {
				// do not move anything if either nothing is selected or only the last item is selected
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
			
			if (!Platform.MAC_OS) {
				// this doesn't look good on Mac
				list.setTopIndex(selectedItems[0]);	
			}
		}
	}

}
