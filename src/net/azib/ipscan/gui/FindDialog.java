package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

public class FindDialog extends AbstractModalDialog{
	Label messageLabel, indexLabel;
	Button nextButton, previousButton, closeButton;
	int actionFlag;
	public FindDialog(String title, String message){
		actionFlag = -1;
		populateShell();
		shell.setText(title);
		messageLabel.setText(message);
		messageLabel.pack();
	}

	@Override
	protected void populateShell(){
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;

		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setLayout(LayoutHelper.formLayout(10, 10, 4));

		messageLabel = new Label(shell, SWT.NONE);
		messageLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(0), null));

		indexLabel = new Label(shell, SWT.NONE);
		indexLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(messageLabel), null));

		previousButton = new Button(shell, SWT.NONE);
		previousButton.setText(Labels.getLabel("button.left"));
		previousButton.setLayoutData(LayoutHelper.formData(new FormAttachment(messageLabel), null, new FormAttachment(messageLabel), null));

		nextButton = new Button(shell, SWT.NONE);
		nextButton.setText(Labels.getLabel("button.right"));
		nextButton.setLayoutData(LayoutHelper.formData(new FormAttachment(previousButton), null, new FormAttachment(messageLabel), null));

		closeButton = new Button(shell, SWT.NONE);
		closeButton.setText(Labels.getLabel("button.close"));
		closeButton.setLayoutData(LayoutHelper.formData(new FormAttachment(nextButton), null, new FormAttachment(messageLabel), null));

		nextButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actionFlag = 0;
				shell.dispose();
			}
		});
		previousButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actionFlag = 1;
				shell.dispose();
			}
		});
		closeButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				actionFlag = -1;
				shell.dispose();
			}
		});
	}

	@Override
	protected int getShellStyle(){return super.getShellStyle() | SWT.SHEET;}

	public int open(int currentIndex, int totalIndex){
		indexLabel.setText(currentIndex + "/" + totalIndex);
		indexLabel.pack();
		shell.pack();
		open();
		return actionFlag;
	}
}
