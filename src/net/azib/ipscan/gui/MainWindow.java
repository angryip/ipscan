/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.MainMenu.CommandsMenu;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.actions.ToolsActions;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.util.LayoutHelper;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Main window of Angry IP Scanner.
 * Contains the menu, IP resultTable, status bar (with progress bar) and
 * a Composite area, which can be substituted dynamically based on
 * the selected feeder.
 * 
 * @author Anton Keks
 */
public class MainWindow {
	
	private final Shell shell;
	private final GUIConfig guiConfig;
	
	private Composite feederArea;
	
	private Combo feederSelectionCombo;
	private FeederGUIRegistry feederRegistry;
	private Control prefsButton;
	private Control fetchersButton;
		
	/**
	 * Creates and initializes the main window.
	 */
	public MainWindow(Shell shell, GUIConfig guiConfig, Composite feederArea, Composite controlsArea, Combo feederSelectionCombo, Button startStopButton, StartStopScanningAction startStopScanningAction, ResultTable resultTable, StatusBar statusBar, CommandsMenu resultsContextMenu, FeederGUIRegistry feederGUIRegistry, StateMachine stateMachine, ToolsActions.Preferences preferencesListener, ToolsActions.ChooseFetchers chooseFetchersListsner) {
		this.shell = shell;
		this.guiConfig = guiConfig;
		
		initShell(shell);
		
		initFeederArea(feederArea, feederGUIRegistry);
		
		initControlsArea(controlsArea, feederSelectionCombo, startStopButton, startStopScanningAction, preferencesListener, chooseFetchersListsner);
		
		initTableAndStatusBar(resultTable, resultsContextMenu, statusBar);

		// after all controls are initialized, resize and open
		shell.setBounds(guiConfig.getMainWindowBounds());
		shell.open();
		if (guiConfig.isMainWindowMaximized) {
			shell.setMaximized(true);
		}
		else {
			// set bounds twice - a workaround for a bug in SWT GTK + Compiz 
			// (otherwise window gets smaller and smaller each time)
			shell.setBounds(guiConfig.getMainWindowBounds());			
		}
		
		if (guiConfig.isFirstRun) {
			if (Platform.CRIPPLED_WINDOWS) {
				// inform crippled windows owners of their default configuration
				MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				box.setText(Version.NAME);
				box.setMessage(Labels.getLabel("text.crippledWindowsInfo"));
				box.open();
			}
			new GettingStartedDialog().open();
			guiConfig.isFirstRun = false;
		}

		stateMachine.addTransitionListener(new EnablerDisabler());
	}

	/**
	 * This method initializes shell
	 */
	private void initShell(final Shell shell) {
		FormLayout formLayout = new FormLayout();
		shell.setLayout(formLayout);
		
		// load and set icon
		Image image = new Image(shell.getDisplay(), Labels.getInstance().getImageAsStream("icon"));
		shell.setImage(image);
				
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				// save dimensions!
				guiConfig.setMainWindowBounds(shell.getBounds(), shell.getMaximized());
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
		resultTable.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(feederArea), new FormAttachment(statusBar.getComposite(), -2)));
		resultTable.setMenu(resultsContextMenu);
	}

	private void initFeederArea(Composite feederArea, FeederGUIRegistry feederRegistry) {
		// feederArea is the placeholder for the visible feeder
		this.feederArea = feederArea;
		feederArea.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(0), null));

		this.feederRegistry = feederRegistry;		
	}

	/**
	 * This method initializes main controls of the main window	
	 */
	private void initControlsArea(final Composite controlsArea, final Combo feederSelectionCombo, final Button startStopButton, final StartStopScanningAction startStopScanningAction, final ToolsActions.Preferences preferencesListener, final ToolsActions.ChooseFetchers chooseFetchersListsner) {
		controlsArea.setLayoutData(LayoutHelper.formData(new FormAttachment(feederArea), new FormAttachment(100), new FormAttachment(0), new FormAttachment(feederArea, 0, SWT.BOTTOM)));
		
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.marginLeft = 7;
		rowLayout.spacing = 3;
		controlsArea.setLayout(rowLayout);
		
		// steal the height from the second child of the FeederGUI - this must be the first edit box.
		// this results in better visual alignment with FeederGUIs
		int controlHeight = feederRegistry.current().getChildren()[1].getSize().y + 1;
				
		// start/stop button
		shell.setDefaultButton(startStopButton);
		startStopButton.setLayoutData(new RowData(SWT.DEFAULT, controlHeight));
		startStopButton.addSelectionListener(startStopScanningAction);
		
		// feeder selection combobox
		this.feederSelectionCombo = feederSelectionCombo;
		feederSelectionCombo.setLayoutData(new RowData(SWT.DEFAULT, controlHeight));
		for (AbstractFeederGUI feederGUI : feederRegistry) {
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();		
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(guiConfig.activeFeeder);
		feederSelectionCombo.setToolTipText(Labels.getLabel("combobox.feeder.tooltip"));
		feederSelectionListener.widgetSelected(null);
		
		((RowData)startStopButton.getLayoutData()).height = feederSelectionCombo.getBounds().height;
		((RowData)startStopButton.getLayoutData()).width = feederSelectionCombo.getBounds().width;
		
		// traverse the button before the combo (and don't traverse other buttons at all)
		controlsArea.setTabList(new Control[] {startStopButton, feederSelectionCombo});
		
		// initialize global standard button height
		guiConfig.standardButtonHeight = feederSelectionCombo.getBounds().height;
		
		int toolbarHeight = guiConfig.standardButtonHeight;
		int toolbarWidth = guiConfig.standardButtonHeight + (Platform.MAC_OS ? 20 : 0);
		
		Image imagePrefs = new Image(null, Labels.getInstance().getImageAsStream("button.preferences.img"));
		Image imageFetchers = new Image(null, Labels.getInstance().getImageAsStream("button.fetchers.img"));
		
		if (!Platform.MAC_OS) {
			prefsButton = new Label(controlsArea, SWT.CENTER);
			((Label) prefsButton).setImage(imagePrefs);
			fetchersButton = new Label(controlsArea, SWT.CENTER);
			((Label) fetchersButton).setImage(imageFetchers);
		}
		else {
			// Mac has buttons - labels with images don't look good in this layout
			prefsButton = new Button(controlsArea, SWT.CENTER);
			((Button) prefsButton).setImage(imagePrefs);
			fetchersButton = new Button(controlsArea, SWT.CENTER);
			((Button) fetchersButton).setImage(imageFetchers);
		}
		
		prefsButton.setToolTipText(Labels.getLabel("title.preferences"));
		prefsButton.setLayoutData(new RowData(toolbarWidth, toolbarHeight));
		prefsButton.setCursor(prefsButton.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		prefsButton.addListener(SWT.MouseDown, preferencesListener);
		
		fetchersButton.setToolTipText(Labels.getLabel("title.fetchers.select"));
		fetchersButton.setLayoutData(new RowData(toolbarWidth, toolbarHeight));
		fetchersButton.setCursor(fetchersButton.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		fetchersButton.addListener(SWT.MouseDown, chooseFetchersListsner);
	}
			
	/**
	 * IP Feeder selection listener. Updates the GUI according to the IP Feeder selection.
	 */
	final class IPFeederSelectionListener implements SelectionListener {
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			feederRegistry.select(feederSelectionCombo.getSelectionIndex());
						
			// all this 'magic' is needed in order to resize everything properly
			// and accommodate feeders with different sizes
			Rectangle bounds = feederRegistry.current().getBounds();
			FormData feederAreaLayoutData = ((FormData)feederArea.getLayoutData());
			feederAreaLayoutData.height = bounds.height;
			feederAreaLayoutData.width = bounds.width;
			shell.layout();
			
			// reset main window title
			shell.setText(feederRegistry.current().getFeederName() + " - " + Version.NAME);
		}
	}
	
	class EnablerDisabler implements StateTransitionListener {
		public void transitionTo(final ScanningState state) {
			if (state != ScanningState.SCANNING && state !=  ScanningState.IDLE)
				return;
			
			shell.getDisplay().asyncExec(new Runnable() {
				public void run() {
					boolean enabled = state == ScanningState.IDLE;
					feederSelectionCombo.setEnabled(enabled);
					prefsButton.setEnabled(enabled);
					fetchersButton.setEnabled(enabled);
				}
			});
		}
	}

}
