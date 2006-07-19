/**
 * 
 */
package net.azib.ipscan.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FileFeederGUI;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
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
	
	private ResultTable resultTable;
	
	private MainMenu mainMenu;
	private Combo feederSelectionCombo;
	private List feederGUIList;
	private Composite feederArea;
	private AbstractFeederGUI feederGUI;
	
	private Composite controlsArea;
	
	private Composite statusBar;
	private ProgressBar progressBar;
	private Label statusText;
	private Label threadsText;
	
	/**
	 * Creates and initializes the main window.
	 */
	public MainWindow() {
		createShell();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		FormLayout formLayout = new FormLayout();
		shell = new Shell();
		shell.setLayout(formLayout);
		shell.setText(Version.FULL_NAME);
		
		// load and set icon
		Image image = new Image(shell.getDisplay(), Labels.getInstance().getImageAsStream("icon"));
		shell.setImage(image);
		
		createMenu();
		createControls();
		createStatusBar();
		createTable();
		
		shell.setSize(new Point(500, 280));
		shell.open();
	}

	private void createMenu() {		
		mainMenu = new MainMenu(this);
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}
	
	public AbstractFeederGUI getFeederGUI() {
		return feederGUI;
	}
	
	/**
	 * Selects the appropriate feeder by it's name
	 */
	public void selectFeederGUI(String feederName) {
		String[] items = feederSelectionCombo.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(feederName)) {
				// select the feeder if found
				feederSelectionCombo.select(i);
				feederSelectionCombo.notifyListeners(SWT.Selection, null);
				return;
			}
		}
		// if not found
		throw new FeederException("No such feeder found: " + feederName);
	}

	public ResultTable getResultTable() {
		return resultTable;
	}

	public Label getStatusText() {
		return statusText;
	}
	
	/**
	 * This method initializes resultTable	
	 */
	private void createTable() {
		resultTable = new ResultTable(shell, mainMenu.getColumnsPopupMenu());
		FormData formData = new FormData();
		formData.top = new FormAttachment(feederArea);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(statusBar, 0);
		resultTable.setLayoutData(formData);
		resultTable.setMenu(mainMenu.getResultsContextMenu());
	}

	/**
	 * This method initializes feederGUI	
	 */
	private void createControls() {
		
		// feederArea is the placeholder for the visible feeder
		feederArea = new Composite(shell, SWT.NONE);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		feederArea.setLayoutData(formData);
		
		// create feeder GUIs
		// TODO: move this code to a more appropriate place
		feederGUIList = new ArrayList();
		feederGUI = new RangeFeederGUI(feederArea);
		feederGUI.setVisible(false);
		feederGUIList.add(feederGUI);
		feederGUI = new RandomFeederGUI(feederArea);
		feederGUI.setVisible(false);
		feederGUIList.add(feederGUI);
		feederGUI = new FileFeederGUI(feederArea);
		feederGUI.setVisible(false);
		feederGUIList.add(feederGUI);
		
		controlsArea = new Composite(shell, SWT.NONE);
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
		button.setLayoutData(new RowData(SWT.DEFAULT, SWT.DEFAULT));
		button.addSelectionListener(new StartStopScanningAction(this, button));
		
		// feeder selection combobox
		feederSelectionCombo = new Combo(controlsArea, SWT.READ_ONLY);
		feederSelectionCombo.setLayoutData(new RowData(SWT.DEFAULT, SWT.DEFAULT));
		for (Iterator i = feederGUIList.iterator(); i.hasNext();) {
			AbstractFeederGUI feederGUI = (AbstractFeederGUI) i.next();
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();		
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(Config.getGlobal().activeFeeder);
		feederSelectionListener.widgetSelected(null);
		
		((RowData)button.getLayoutData()).height = feederSelectionCombo.getBounds().height;
		((RowData)button.getLayoutData()).width = feederSelectionCombo.getBounds().width;
	}

	/**
	 * This method initializes status bar and it's controls
	 */
	private void createStatusBar() {
		statusBar = new Composite(shell, SWT.NONE);
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.height = 18;
		formData.bottom = new FormAttachment(100);
		statusBar.setLayoutData(formData);
		RowLayout rowLayout = new RowLayout();
		rowLayout.fill = true;
		rowLayout.wrap = false;
		rowLayout.spacing = 0;
		statusBar.setLayout(/*rowLayout*/ new FillLayout());
		
		statusText = new Label(statusBar, SWT.BORDER);
		//statusText.setLayoutData(new RowData(150, SWT.DEFAULT));
		setStatusText(null);
		
		threadsText = new Label(statusBar, SWT.BORDER);
		//threadsText.setLayoutData(new RowData(50, SWT.DEFAULT));
		threadsText.setText(Labels.getInstance().getString("text.threads") + "0");
		
		progressBar = new ProgressBar(statusBar, SWT.BORDER);
		//progressBar.setLayoutData(new RowData());
		progressBar.setSelection(0);
	}
	
	public void setStatusText(String statusText) {
		if (statusText == null) {
			statusText = Labels.getInstance().getString("state.ready"); 
		}
		if (!this.statusText.isDisposed())
			this.statusText.setText(statusText);
	}
	
	public void setRunningThreads(int runningThreads) {
		if (!threadsText.isDisposed()) 
			// TODO: make this more efficient
			threadsText.setText(Labels.getInstance().getString("text.threads") + runningThreads);
	}
	
	public void setProgress(int progress) {
		if (!progressBar.isDisposed())
			progressBar.setSelection(progress);
	}
	
	/**
	 * IP Feeder selection listener. Updates the GUI according to the IP Feeder selection.
	 */
	private final class IPFeederSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			// hide current feeder
			feederGUI.setVisible(false);
			// get new feeder
			int newActiveFeeder = feederSelectionCombo.getSelectionIndex();
			feederGUI = (AbstractFeederGUI) feederGUIList.get(newActiveFeeder);
			Config.getGlobal().activeFeeder = newActiveFeeder;
			// make new feeder visible
			feederGUI.setVisible(true);
			
			// all this 'magic' is needed in order to resize everything properly
			// and accomodate feeders with different sizes
			Rectangle bounds = feederGUI.getBounds();
			FormData feederAreaLayoutData = ((FormData)feederArea.getLayoutData());
			feederAreaLayoutData.height = bounds.height;
			feederAreaLayoutData.width = bounds.width;
			shell.layout();
		}
	}

}
