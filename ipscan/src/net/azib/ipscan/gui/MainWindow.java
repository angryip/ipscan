/**
 * 
 */
package net.azib.ipscan.gui;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Main window of Angry IP Scanner.
 * Contains the menu, IP resultTable, status bar (with progress bar) and
 * a Composite area, which can be substituted dynamically based on
 * the selected feeder.
 * 
 * @author anton
 */
public class MainWindow {
	
	private Shell shell;
	
	private Composite feederArea;
	
	private Combo feederSelectionCombo;
	private FeederGUIRegistry feederRegistry;
		
	/**
	 * Creates and initializes the main window.
	 */
	public MainWindow(Shell shell, Composite feederArea, Composite controlsArea, Combo feederSelectionCombo, Button startStopButton, StartStopScanningAction startStopScanningAction, ResultTable resultTable, StatusBar statusBar, CommandsMenu resultsContextMenu, FeederGUIRegistry feederGUIRegistry) {
		
		initShell(shell);
		
		initFeederArea(feederArea, feederGUIRegistry);
		
		initControlsArea(controlsArea, feederSelectionCombo, startStopButton, startStopScanningAction);
		
		initTableAndStatusBar(resultTable, resultsContextMenu, statusBar);

		// after all controls are initialized, resize and open
		shell.setBounds(Config.getDimensionsConfig().getWindowBounds());
		shell.setMaximized(Config.getDimensionsConfig().isWindowMaximized);
		shell.open();		
	}

	/**
	 * This method initializes shell
	 */
	private void initShell(final Shell shell) {
		this.shell = shell;
		
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		shell.setText(Version.FULL_NAME);
		
		// load and set icon
		Image image = new Image(shell.getDisplay(), Labels.getInstance().getImageAsStream("icon"));
		shell.setImage(image);
				
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				// save dimensions!
				Config.getDimensionsConfig().setWindowBounds(shell.getBounds(), shell.getMaximized());
			}
		});
	}

	/**
	 * @return the underlying shell, used by the Main class
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * @return true if the underlying shell is disposed
	 */
	public boolean isDisposed() {
		return shell.isDisposed();
	}

	/**
	 * This method initializes resultTable	
	 */
	private void initTableAndStatusBar(ResultTable resultTable, CommandsMenu resultsContextMenu, StatusBar statusBar) {
		FormData formData = new FormData();
		formData.top = new FormAttachment(feederArea);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(statusBar.getComposite(), -3);
		resultTable.setLayoutData(formData);
		resultTable.setMenu(resultsContextMenu);
	}

	private void initFeederArea(Composite feederArea, FeederGUIRegistry feederRegistry) {
		// feederArea is the placeholder for the visible feeder
		this.feederArea = feederArea;
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		feederArea.setLayoutData(formData);

		this.feederRegistry = feederRegistry;		
	}

	/**
	 * This method initializes main controls of the main window	
	 */
	private void initControlsArea(Composite controlsArea, Combo feederSelectionCombo, Button startStopButton, StartStopScanningAction startStopScanningAction) {
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(feederArea);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(feederArea, 0, SWT.BOTTOM);
		controlsArea.setLayoutData(formData);
		
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.marginLeft = 7;
		controlsArea.setLayout(rowLayout);
				
		// start/stop button
		shell.setDefaultButton(startStopButton);
		startStopButton.setLayoutData(new RowData(SWT.DEFAULT, 23));
		startStopButton.addSelectionListener(startStopScanningAction);
		
		// feeder selection combobox
		this.feederSelectionCombo = feederSelectionCombo;
		feederSelectionCombo.setLayoutData(new RowData(SWT.DEFAULT, 23));
		for (Iterator i = feederRegistry.iterator(); i.hasNext();) {
			AbstractFeederGUI feederGUI = (AbstractFeederGUI) i.next();
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();		
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(Config.getGlobal().activeFeeder);
		feederSelectionCombo.setToolTipText(Labels.getLabel("combobox.feeder.tooltip"));
		feederSelectionListener.widgetSelected(null);
		
		((RowData)startStopButton.getLayoutData()).height = feederSelectionCombo.getBounds().height;
		((RowData)startStopButton.getLayoutData()).width = feederSelectionCombo.getBounds().width;
	}
			
	/**
	 * IP Feeder selection listener. Updates the GUI according to the IP Feeder selection.
	 */
	private final class IPFeederSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			feederRegistry.select(feederSelectionCombo.getSelectionIndex());
						
			// all this 'magic' is needed in order to resize everything properly
			// and accomodate feeders with different sizes
			Rectangle bounds = feederRegistry.current().getBounds();
			FormData feederAreaLayoutData = ((FormData)feederArea.getLayoutData());
			feederAreaLayoutData.height = bounds.height;
			feederAreaLayoutData.width = bounds.width;
			shell.layout();
		}
	}

}
