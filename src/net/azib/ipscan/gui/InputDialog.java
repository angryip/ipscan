/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Customizable InputDialog
 *
 * @author Anton Keks
 */
public class InputDialog extends AbstractModalDialog {

	private Label messageLabel = null;
	private Text text = null;
	private Button okButton = null;
	private Button cancelButton = null;
	
	private String message;
	
	public InputDialog(String title, String message) {
		createShell();
		shell.setText(title);
		messageLabel.setText(message);
		messageLabel.pack();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));

		messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(0), null));
		
		text = new Text(shell, SWT.BORDER);
		
		okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtonsInFormLayout(okButton, cancelButton, text);
		
		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				message = text.getText();
				shell.dispose();
			}
		});
		cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				message = null;
				shell.dispose();
			}
		});
	}
	
	private void setText(String text) {
		if (text != null) {
			this.text.setText(text);
			this.text.setSelection(0, -1);
			this.text.pack();
			this.text.setLayoutData(LayoutHelper.formData(Math.max(this.text.getSize().x, 310), SWT.DEFAULT, new FormAttachment(0), null, new FormAttachment(messageLabel), null));
			this.text.setFocus();
			shell.pack();
		}
	}

	/**
	 * Opens the dialog and waits for user to input the data.
	 * 
	 * @return the entered text or null in case of cancel.
	 */
	public String open(String text, String okButtonText) {
		okButton.setText(okButtonText);
		setText(text);
		super.open();
		return message;
	}
	/**
	 * Opens the dialog and waits for user to input the data.
	 * 
	 * @return the entered text or null in case of cancel.
	 */
	public String open(String text) {
		return open(text, Labels.getLabel("button.OK"));
	}

}
