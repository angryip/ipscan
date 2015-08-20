/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.GUIConfig.DisplayMethod;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.gui.actions.CommandsMenuActions.Delete;
import net.azib.ipscan.gui.actions.ToolsActions.SelectDead;
import net.azib.ipscan.gui.actions.ToolsActions.SelectWithoutPorts;
import net.azib.ipscan.gui.actions.ToolsActions.TableSelection;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import static net.azib.ipscan.config.GUIConfig.DisplayMethod.PORTS;

/**
 * The status bar of the main window.
 *
 * @author Anton Keks
 */
@Singleton
public class StatusBar {
	private Composite composite;
	private Label statusText;
	private Label displayMethodText;
	private Label threadsText;
	private boolean maxThreadsReachedBefore;
	private ProgressBar progressBar;
	
	private ScannerConfig scannerConfig;
	private GUIConfig guiConfig;
	private StateMachine stateMachine;
	private ResultTable resultTable;

	@Inject public StatusBar(Shell shell, GUIConfig guiConfig, ScannerConfig scannerConfig, ResultTable resultTable, StateMachine stateMachine) {
		this.guiConfig = guiConfig;
		this.scannerConfig = scannerConfig;
		this.stateMachine = stateMachine;
		this.resultTable = resultTable;
		this.resultTable.addListener(SWT.Selection, new TableSelection(this, stateMachine));
		
		composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), null, new FormAttachment(100)));
		
		composite.setLayout(LayoutHelper.formLayout(1, 1, 2));
		
		statusText = new Label(composite, SWT.BORDER);
		statusText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(35), new FormAttachment(0), new FormAttachment(100)));
		setStatusText(null);
		
		displayMethodText = new Label(composite, SWT.BORDER);
		displayMethodText.setText(Labels.getLabel("text.display." + PORTS));
		displayMethodText.pack();
		displayMethodText.setLayoutData(LayoutHelper.formData(displayMethodText.getSize().x, SWT.DEFAULT, new FormAttachment(statusText), null, new FormAttachment(0), new FormAttachment(100)));
		displayMethodText.addListener(SWT.MouseDown, new DisplayModeChangeListener());
		updateConfigText();

		threadsText = new Label(composite, SWT.BORDER);
		setRunningThreads(Math.min(scannerConfig.maxThreads, 200)); // this should set the longest possible text		
		threadsText.pack(); // calculate the width
		threadsText.setLayoutData(LayoutHelper.formData(threadsText.getSize().x, SWT.DEFAULT, new FormAttachment(displayMethodText), null, new FormAttachment(0), new FormAttachment(100)));
		setRunningThreads(0); // set back to 0 at startup
		
		progressBar = new ProgressBar(composite, SWT.BORDER);
		progressBar.setLayoutData(LayoutHelper.formData(new FormAttachment(threadsText), new FormAttachment(100, 0), new FormAttachment(0), new FormAttachment(100)));
		progressBar.setSelection(0);
	}
	
	/**
	 * Updates config text according to the latest changes in the GlobalConfig
	 */
	public void updateConfigText() {
		displayMethodText.setText(Labels.getLabel("text.display." + guiConfig.displayMethod));
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
			boolean maxThreadsReached = runningThreads == scannerConfig.maxThreads;
			if (maxThreadsReachedBefore || maxThreadsReached) {
				Color newColor = threadsText.getDisplay().getSystemColor(maxThreadsReached ? SWT.COLOR_DARK_RED : SWT.COLOR_WIDGET_FOREGROUND);
				threadsText.setForeground(newColor);
			}
			maxThreadsReachedBefore = maxThreadsReached;
			
			threadsText.setText(Labels.getLabel("text.threads") + runningThreads + 
					(maxThreadsReached ? Labels.getLabel("text.threads.max") : ""));
		}
	}
	
	public void setProgress(int progress) {
		if (!progressBar.isDisposed())
			progressBar.setSelection(progress);
	}
	
	public Shell getShell() {
		return composite.getShell();
	}
	
	public void setEnabled(boolean enabled) {
		// enable/disable interactive controls on the status bar
		displayMethodText.setEnabled(enabled);
	}

	class DisplayModeChangeListener implements Listener {
		public void handleEvent(Event event) {
			// user clicked the config text, lets ask the display options
			if (event.type == SWT.MouseDown) {
				Menu popupMenu = new Menu(getShell(), SWT.POP_UP);
				for (DisplayMethod displayMethod : DisplayMethod.values()) {
					MenuItem item = new MenuItem(popupMenu, 0);
					item.setText(Labels.getLabel("text.display." + displayMethod));
					item.setData(displayMethod);
					item.addListener(SWT.Selection, this);
				}
				popupMenu.setVisible(true);
			}
			// handle menu item selection
			else if (event.type == SWT.Selection) {
				// remember the selected display method
				guiConfig.displayMethod = (DisplayMethod) event.widget.getData();
				updateConfigText();
				if (!resultTable.getScanningResults().areResultsAvailable()) return;
				switch (guiConfig.displayMethod) {
					case ALIVE: {
						new SelectDead(resultTable).handleEvent(event);
						new Delete(resultTable, stateMachine).handleEvent(event);
						break;
					}
					case PORTS: {
						new SelectWithoutPorts(resultTable).handleEvent(event);
						new Delete(resultTable, stateMachine).handleEvent(event);
						break;
					}
				}
			}
		}
	}
}
