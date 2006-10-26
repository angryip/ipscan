/**
 * 
 */
package net.azib.ipscan.gui;

import java.util.Iterator;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
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
	private ResultTable resultTable;
	
	private MainMenu mainMenu;
	private Combo feederSelectionCombo;
	private FeederGUIRegistry feederRegistry;
	
	private Composite controlsArea;
	
	private StatusBar statusBar;
	
	/**
	 * Creates and initializes the main window.
	 */
	public MainWindow(Shell shell, Composite feederArea, Composite controlsArea, Combo feederSelectionCombo, ResultTable resultTable, StatusBar statusBar, MainMenu mainMenu, FeederGUIRegistry feederGUIRegistry) {
		
		this.shell = shell;
		this.feederArea = feederArea;
		this.controlsArea = controlsArea;
		this.feederSelectionCombo = feederSelectionCombo;
		this.resultTable = resultTable;
		this.statusBar = statusBar;
		this.mainMenu = mainMenu;
		this.feederRegistry = feederGUIRegistry;
		
		populateShell();
		
		createControls();
		
		initTable();
		
		shell.setBounds(Config.getDimensionsConfig().getWindowBounds());
		shell.setMaximized(Config.getDimensionsConfig().isWindowMaximized);
		shell.open();
	}

	/**
	 * This method initializes shell
	 */
	private void populateShell() {
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

	public Shell getShell() {
		return shell;
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	/**
	 * @deprecated use FeederGUIRegistry instead
	 */
	public AbstractFeederGUI getFeederGUI() {
		return feederRegistry.current();
	}
	
	/**
	 * @deprecated
	 */
	public ResultTable getResultTable() {
		return resultTable;
	}

	/**
	 * This method initializes resultTable	
	 */
	private void initTable() {
		resultTable.initialize(mainMenu.getColumnsPopupMenu());
		FormData formData = new FormData();
		formData.top = new FormAttachment(feederArea);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(statusBar.getComposite(), -3);
		resultTable.setLayoutData(formData);
		resultTable.setMenu(mainMenu.getResultsContextMenu());
	}

	/**
	 * This method initializes feederGUI	
	 */
	private void createControls() {
		
		// feederArea is the placeholder for the visible feeder
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		feederArea.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(feederArea);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(feederArea, 0, SWT.BOTTOM);
		controlsArea.setLayoutData(formData);
		
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.marginLeft = 7;
		controlsArea.setLayout(rowLayout);
				
		// start/stop button
		Button button = new Button(controlsArea, SWT.NONE);
		shell.setDefaultButton(button);
		button.setLayoutData(new RowData(SWT.DEFAULT, 23));
		button.addSelectionListener(new StartStopScanningAction(this, button));
		
		// feeder selection combobox
		feederSelectionCombo.setLayoutData(new RowData(SWT.DEFAULT, 23));
		for (Iterator i = feederRegistry.iterator(); i.hasNext();) {
			AbstractFeederGUI feederGUI = (AbstractFeederGUI) i.next();
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();		
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(Config.getGlobal().activeFeeder);
		feederSelectionCombo.setToolTipText(Labels.getInstance().getString("combobox.feeder.tooltip"));
		feederSelectionListener.widgetSelected(null);
		
		((RowData)button.getLayoutData()).height = feederSelectionCombo.getBounds().height;
		((RowData)button.getLayoutData()).width = feederSelectionCombo.getBounds().width;
	}
	
	/**
	 * @deprecated use statusBar instead
	 */
	public void setStatusText(String statusText) {
		statusBar.setStatusText(statusText);
	}
	
	/**
	 * @deprecated use statusBar instead
	 */
	public void setRunningThreads(int runningThreads) {
		statusBar.setRunningThreads(runningThreads);
	}
	
	/**
	 * @deprecated use statusBar instead
	 */
	public void setProgress(int progress) {
		statusBar.setProgress(progress);
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
