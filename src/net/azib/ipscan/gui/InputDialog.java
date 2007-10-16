/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;

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
		shell.setSize(new Point(310, 125));
		shell.setLayout(null);
		messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setBounds(new Rectangle(10, 10, 282, 14));
		
		okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));
		
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));
		
		positionButtons(okButton, cancelButton);
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(new Rectangle(10, 28, shell.getClientArea().width - 20, 24));

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
		
		text.setFocus();
	}
	
	private void setText(String text) {
		this.text.setText(text);
		this.text.setSelection(0, -1);
	}

	/**
	 * Opens the dialog and waits for user to input the data.
	 * 
	 * @return the entered text or null in case of cancel.
	 */
	public String open(String text) {
		setText(text);
		super.open();
		return message;
	}

}
