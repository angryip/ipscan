/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import java.util.logging.Logger;

/**
 * This is the base of a modal dialog window
 *
 * @author Anton Keks
 */
public abstract class AbstractModalDialog {
	protected Shell shell;
	private static Image icon;
	
	public void open() {
		if (shell == null || shell.isDisposed()) {
			createShell();
		}
		
		// center dialog box according to the parent window
		if (shell.getParent() != null) {
			var parentBounds = shell.getParent().getBounds();
			var childBounds = shell.getBounds();
			var x = Math.max(0, parentBounds.x + (parentBounds.width - childBounds.width) / 2);
			var y = Math.max(0, parentBounds.y + (parentBounds.height - childBounds.height) / 2);
			shell.setLocation(x, y);
		}
		
		// open the dialog box 
		shell.open();
		
		// create a separate event loop
		var display = Display.getCurrent();
		while (shell != null && !shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		// forget the reference to the shell (this class is reused in the container)
		shell = null;
	}

	protected void close() {
		if (shell != null && !shell.isDisposed()) {
			shell.close();
			shell.dispose();
		}
	}
	
	/**
	 * Populates the newly created shell with controls
	 */
	protected abstract void populateShell();

	protected final void createShell() {
		Shell parent = null;
		try {
			parent = Display.getCurrent().getShells()[0];
		}
		catch (Exception e) {
			Logger.getLogger(getClass().getName()).warning("Failed to get parent shell: " + e);
		}
		
		shell = new Shell(parent, getShellStyle());
		if (icon == null && parent != null) icon = parent.getImage();
		if (icon == null) icon = new Image(shell.getDisplay(), getClass().getResourceAsStream("/images/icon.png"));
		shell.setImage(icon);
		
		populateShell();
	}

	/**
	 * @return combined style constants of the shell to be created
	 */
	protected int getShellStyle() {
		return SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM;
	}

	/**
	 * Positions 2 buttons at the bottom-right part of the shell.
	 * On MacOS also changes OK and cancel button order.
	 * @param cancelButton can be null
	 */
	protected void positionButtons(Button okButton, Button cancelButton) {
		shell.setDefaultButton(okButton);
		var clientArea = shell.getClientArea();
		
		var size = okButton.computeSize(85, SWT.DEFAULT);
		okButton.setSize(size);
		
		if (cancelButton != null) {
			cancelButton.setSize(size);
		
			if (Platform.MAC_OS || Platform.LINUX) {
				// Mac OS and Linux users expect button order to be reverse
				var fooButton = okButton;
				okButton = cancelButton;
				cancelButton = fooButton;
			}
			// both buttons
			var distance = size.y / 3;
			cancelButton.setLocation(clientArea.width - size.x - 10, clientArea.height - size.y - 10);
			okButton.setLocation(clientArea.width - size.x * 2 - 10 - distance, clientArea.height - size.y - 10);	
		}
		else {
			// only one button
			okButton.setLocation(clientArea.width - size.x - 10, clientArea.height - size.y - 10);
		}
	}
		
	/**
	 * Positions 2 buttons at the bottom-right part of the shell in the FormLayout.
	 * On MacOS also changes OK and cancel button order.
	 * @param control the bottom-right widget, used as a guide
	 */
	protected void positionButtonsInFormLayout(Button okButton, Button cancelButton, Control control) {
		shell.setDefaultButton(okButton);
		
		if (Platform.MAC_OS || Platform.LINUX) {
			// Mac OS and Linux users expect button order to be reverse
			var fooButton = okButton;
			okButton = cancelButton;
			cancelButton = fooButton;
		}
		// both buttons
		cancelButton.pack();
		cancelButton.setLayoutData(LayoutHelper.formData(Math.max(85, cancelButton.getSize().x),  SWT.DEFAULT, null, new FormAttachment(control, 0, SWT.RIGHT), new FormAttachment(control, 8), null));
		okButton.pack();
		var okSize = okButton.getSize();
		okButton.setLayoutData(LayoutHelper.formData(Math.max(85, okSize.x), SWT.DEFAULT, null, new FormAttachment(cancelButton, -okSize.y/3), new FormAttachment(control, 8), null));
	}
		
	/**
	 * Adds an optional close button, depending on the platform.
	 */
	protected Button createCloseButton() {
		var button = new Button(shell, SWT.NONE);
		button.setText(Labels.getLabel("button.close"));
		positionButtons(button, null);
		button.addListener(SWT.Selection, event -> close());
		button.setFocus();
		return button;
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

			moveSelection(list, -1);
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

			moveSelection(list, 1);
		}
	}

	/**
	 * Moves all selected items one step up (-1) or down (+1) as a group,
	 * without corrupting the list when multiple (possibly non-contiguous) items are selected.
	 */
	private static void moveSelection(List list, int direction) {
		var items = list.getItems();
		var selected = list.getSelectionIndices();
		if (selected.length == 0) return;

		var newOrder = new java.util.ArrayList<String>(java.util.Arrays.asList(items));

		if (direction < 0) {
			// moving up: process from top to bottom
			for (var i = 0; i < selected.length; i++) {
				var idx = selected[i];
				if (idx > 0 && !list.isSelected(idx - 1)) {
					// swap with the previous item (only if it is not itself being moved)
					var tmp = newOrder.get(idx - 1);
					newOrder.set(idx - 1, newOrder.get(idx));
					newOrder.set(idx, tmp);
				}
			}
		}
		else {
			// moving down: process from bottom to top
			for (var i = selected.length - 1; i >= 0; i--) {
				var idx = selected[i];
				if (idx < newOrder.size() - 1 && !list.isSelected(idx + 1)) {
					var tmp = newOrder.get(idx + 1);
					newOrder.set(idx + 1, newOrder.get(idx));
					newOrder.set(idx, tmp);
				}
			}
		}

		list.setItems(newOrder.toArray(new String[0]));
		list.select(selected);
	}

}
