/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * The status bar of the main window.
 *
 * @author Anton Keks
 */
public class StatusBar {
	
	private Composite composite;
	
	private Label statusText;
	private Label configText;
	private Label threadsText;
	private boolean maxThreadsReached;
	private ProgressBar progressBar;
	
	private ScannerConfig scannerConfig;
	private GUIConfig guiConfig;

	public StatusBar(Shell shell, GUIConfig guiConfig, ScannerConfig scannerConfig) {
		this.guiConfig = guiConfig;
		this.scannerConfig = scannerConfig;
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(LayoutHelper.formData(SWT.DEFAULT, 19, new FormAttachment(0), new FormAttachment(100), null, new FormAttachment(100)));
		
		composite.setLayout(LayoutHelper.formLayout(1, 1, 2));
		
		statusText = new Label(composite, SWT.BORDER);
		statusText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(40), new FormAttachment(0), new FormAttachment(100)));
		setStatusText(null);
		
		configText = new Label(composite, SWT.BORDER);
		configText.setLayoutData(LayoutHelper.formData(120, SWT.DEFAULT, new FormAttachment(statusText), null, new FormAttachment(0), new FormAttachment(100)));
		updateConfigText();

		threadsText = new Label(composite, SWT.BORDER);
		threadsText.setLayoutData(LayoutHelper.formData(85, SWT.DEFAULT, new FormAttachment(configText), null, new FormAttachment(0), new FormAttachment(100)));
		threadsText.setText(Labels.getLabel("text.threads") + "0");
		
		progressBar = new ProgressBar(composite, SWT.BORDER);
		progressBar.setLayoutData(LayoutHelper.formData(new FormAttachment(threadsText), new FormAttachment(100, Platform.MAC_OS ? -20 : 0), new FormAttachment(0), new FormAttachment(100)));
		progressBar.setSelection(0);
	}
	
	/**
	 * Updates config text according to the latest changes in the GlobalConfig
	 */
	public void updateConfigText() {
		configText.setText(Labels.getLabel("text.display." + guiConfig.displayMethod));
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
		if (!threadsText.isDisposed()) { 
			// TODO: make this more efficient
			
			if (maxThreadsReached || runningThreads == scannerConfig.maxThreads) {
				Color newColor = threadsText.getDisplay().getSystemColor(maxThreadsReached ? SWT.COLOR_WIDGET_FOREGROUND : SWT.COLOR_DARK_RED);
				threadsText.setForeground(newColor);
			}
			maxThreadsReached = runningThreads == scannerConfig.maxThreads;
			
			threadsText.setText(Labels.getLabel("text.threads") + runningThreads);
		}
	}
	
	public void setProgress(int progress) {
		if (!progressBar.isDisposed())
			progressBar.setSelection(progress);
	}
	
}
