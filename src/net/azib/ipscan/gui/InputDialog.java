/**
 * 
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
 * @author anton
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
		shell.setSize(new Point(300, 112));
		shell.setLayout(null);
		messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setBounds(new Rectangle(3, 5, 282, 14));
		text = new Text(shell, SWT.BORDER);
		text.setBounds(new Rectangle(5, 24, 281, 24));
		okButton = new Button(shell, SWT.NONE);
		okButton.setLocation(new Point(57, 55));
		okButton.setSize(new Point(70, 25));
		okButton.setText(Labels.getInstance().getString("button.OK"));
		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				message = text.getText();
				shell.dispose();
			}
		});
		shell.setDefaultButton(okButton);
		cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setLocation(new Point(155, 55));
		cancelButton.setSize(new Point(70, 25));
		cancelButton.setText(Labels.getInstance().getString("button.cancel"));
		cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				message = null;
				shell.dispose();
			}
		});
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
