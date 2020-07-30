/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

/**
 * Customizable InputDialog
 *
 * @author Anton Keks
 */
public class InputDialog extends AbstractModalDialog {

	Label messageLabel;
	Text text;
	Button okButton;
	Button cancelButton;
	
	private String message;
	
	public InputDialog(String title, String message) {
		populateShell();
		shell.setText(title);
		messageLabel.setText(message);
		messageLabel.pack();
	}

	@Override
	protected void populateShell() {
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

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.SHEET;
	}

	private void setText(String text) {
		text = text != null ? text : "";
		this.text.setText(text);
		this.text.pack();
		this.text.setLayoutData(LayoutHelper.formData(Math.max(this.text.getSize().x, 310), SWT.DEFAULT, new FormAttachment(0), null, new FormAttachment(messageLabel), null));
		this.text.setFocus();
		this.text.setSelection(0, text.length());
	}

	/**
	 * Opens the dialog and waits for user to input the data.
	 * 
	 * @return the entered text or null in case of cancel.
	 */
	public String open(String text, String okButtonText) {
		okButton.setText(okButtonText);
		setText(text);
		// reposition buttons because of changed text
		positionButtonsInFormLayout(okButton, cancelButton, this.text);
		// layout the shell
		shell.pack();
		// time to show!
		open();
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
