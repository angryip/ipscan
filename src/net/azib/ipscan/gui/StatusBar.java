/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * The status bar of the main window.
 *
 * @author anton
 */
public class StatusBar {
	
	private Composite composite;
	
	private ProgressBar progressBar;
	private Label statusText;
	private Label threadsText;

	public StatusBar(Shell shell) {
		composite = new Composite(shell, SWT.NONE);
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.height = 18;
		formData.bottom = new FormAttachment(100);
		composite.setLayoutData(formData);
		RowLayout rowLayout = new RowLayout();
		rowLayout.fill = true;
		rowLayout.wrap = false;
		rowLayout.spacing = 0;
		composite.setLayout(/*rowLayout*/ new FillLayout());
		
		statusText = new Label(composite, SWT.BORDER);
		//statusText.setLayoutData(new RowData(150, SWT.DEFAULT));
		setStatusText(null);
		
		threadsText = new Label(composite, SWT.BORDER);
		//threadsText.setLayoutData(new RowData(50, SWT.DEFAULT));
		threadsText.setText(Labels.getLabel("text.threads") + "0");
		
		progressBar = new ProgressBar(composite, SWT.BORDER);
		//progressBar.setLayoutData(new RowData());
		progressBar.setSelection(0);
	}
	
	/**
	 * Used for the positioning of the controls in the MainWindow
	 */
	Composite getComposite() {
		return composite;
	}
	
	/**
	 * @return true if the underlying widgets are disposed
	 */
	public boolean isDisposed() {
		return composite.isDisposed();
	}
	
	/**
	 * Sets the status bar text displayed to the user.
	 * @param statusText the text to set, null to use the default text (Ready)
	 */
	public void setStatusText(String statusText) {
		if (statusText == null) {
			statusText = Labels.getLabel("state.ready"); 
		}
		if (!this.statusText.isDisposed())
			this.statusText.setText(statusText);
	}

	public void setRunningThreads(int runningThreads) {
		if (!threadsText.isDisposed()) 
			// TODO: make this more efficient
			threadsText.setText(Labels.getLabel("text.threads") + runningThreads);
	}
	
	public void setProgress(int progress) {
		if (!progressBar.isDisposed())
			progressBar.setSelection(progress);
	}
	
}
