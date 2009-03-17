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
import net.azib.ipscan.gui.actions.BrowserLauncher;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

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
	
	private static int buttonHeight = 22;
	private Button startStopButton;
	private Combo feederSelectionCombo;
	private FeederGUIRegistry feederRegistry;
	
	private StatusBar statusBar;
	private ToolBar prefsButton;
	private ToolBar fetchersButton;
		
	/**
	 * Creates and initializes the main window.
	 */
	public MainWindow(Shell shell, GUIConfig guiConfig, Composite feederArea, Composite controlsArea, Combo feederSelectionCombo, Button startStopButton, StartStopScanningAction startStopScanningAction, ResultTable resultTable, StatusBar statusBar, CommandsMenu resultsContextMenu, FeederGUIRegistry feederGUIRegistry, StateMachine stateMachine, ToolsActions.Preferences preferencesListener, ToolsActions.ChooseFetchers chooseFetchersListsner) {
		this.shell = shell;
		this.guiConfig = guiConfig;
		this.statusBar = statusBar;
		
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
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					if (Platform.CRIPPLED_WINDOWS) {
						// inform crippled windows owners of their default configuration
						if (showMessage(Labels.getLabel("text.crippledWindowsInfo"), SWT.YES | SWT.NO) == SWT.YES) {
							BrowserLauncher.openURL(Version.FAQ_CRIPPLED_WINDOWS_URL);
						}
					}
					if (Platform.GNU_JAVA) {
						// show a warning if running under GNU Java
						showMessage(Labels.getLabel("text.gnuJavaInfo"), SWT.OK);
					}
					MainWindow.this.shell.forceActive();
					new GettingStartedDialog().open();
					MainWindow.this.guiConfig.isFirstRun = false;
				}
			});
		}

		stateMachine.addTransitionListener(new EnablerDisabler());
	}
	
	private int showMessage(String text, int buttons) {
		MessageBox box = new MessageBox(MainWindow.this.shell, SWT.ICON_WARNING | buttons);
		box.setText(Version.NAME);
		box.setMessage(text);
		return box.open();
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
		controlsArea.setLayout(LayoutHelper.formLayout(7, 3, 3));

		// steal the height from the second child of the FeederGUI - this must be the first edit box.
		// this results in better visual alignment with FeederGUIs
		Control secondControl = feederRegistry.current().getChildren()[1];
		// initialize global standard button height
		buttonHeight = secondControl.getSize().y + 1;
				
		// feeder selection combobox
		this.feederSelectionCombo = feederSelectionCombo;
		for (AbstractFeederGUI feederGUI : feederRegistry) {
			feederSelectionCombo.add(feederGUI.getFeederName());	
		}
		feederSelectionCombo.pack();
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();		
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(guiConfig.activeFeeder);
		feederSelectionCombo.setToolTipText(Labels.getLabel("combobox.feeder.tooltip"));
		
		// start/stop button
		this.startStopButton = startStopButton;
		shell.setDefaultButton(startStopButton);
		startStopButton.addSelectionListener(startStopScanningAction);

		// traverse the button before the combo (and don't traverse other buttons at all)
		controlsArea.setTabList(new Control[] {startStopButton, feederSelectionCombo});
				
		prefsButton = new ToolBar(controlsArea, SWT.FLAT);
		prefsButton.setCursor(prefsButton.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		ToolItem item = new ToolItem(prefsButton, SWT.PUSH);
		item.setImage(new Image(null, Labels.getInstance().getImageAsStream("button.preferences.img")));
		item.setToolTipText(Labels.getLabel("title.preferences"));
		item.addListener(SWT.Selection, preferencesListener);
		
		fetchersButton = new ToolBar(controlsArea, SWT.FLAT);
		fetchersButton.setCursor(fetchersButton.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		item = new ToolItem(fetchersButton, SWT.PUSH);
		item.setImage(new Image(null, Labels.getInstance().getImageAsStream("button.fetchers.img")));
		item.setToolTipText(Labels.getLabel("title.fetchers.select"));
		item.addListener(SWT.Selection, chooseFetchersListsner);

		feederSelectionListener.widgetSelected(null);
	}
	
	private void relayoutControls() {
		boolean twoRowToolbar = Math.abs(feederRegistry.current().getSize().y - buttonHeight * 2) <= 10;
		
		feederSelectionCombo.setLayoutData(LayoutHelper.formData(SWT.DEFAULT, buttonHeight, new FormAttachment(0), null, new FormAttachment(0), null));
		if (twoRowToolbar) {
			startStopButton.setLayoutData(LayoutHelper.formData(feederSelectionCombo.getSize().x, SWT.DEFAULT, new FormAttachment(0), null, new FormAttachment(feederSelectionCombo), null));
			prefsButton.setLayoutData(LayoutHelper.formData(new FormAttachment(feederSelectionCombo), null, new FormAttachment(feederSelectionCombo, 0, SWT.CENTER), null));
			fetchersButton.setLayoutData(LayoutHelper.formData(new FormAttachment(startStopButton), null, new FormAttachment(startStopButton, 0, SWT.CENTER), null));
		}
		else {
			startStopButton.setLayoutData(LayoutHelper.formData(feederSelectionCombo.getSize().x, SWT.DEFAULT, new FormAttachment(feederSelectionCombo), null, new FormAttachment(0), null));
			prefsButton.setLayoutData(LayoutHelper.formData(new FormAttachment(startStopButton), null, new FormAttachment(feederSelectionCombo, 0, SWT.CENTER), null));
			fetchersButton.setLayoutData(LayoutHelper.formData(new FormAttachment(prefsButton), null, new FormAttachment(startStopButton, 0, SWT.CENTER), null));
		}
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
			relayoutControls();
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
					feederArea.setEnabled(enabled);
					feederSelectionCombo.setEnabled(enabled);
					prefsButton.setEnabled(enabled);
					fetchersButton.setEnabled(enabled);
					statusBar.setEnabled(enabled);
				}
			});
		}
	}
	
	// TODO: remove this class with SWT > 3.5
	public static class FeederSelectionCombo extends Combo {
		public FeederSelectionCombo(Composite parent) {
			super(parent, SWT.READ_ONLY);
		}

		@Override
		public int getTextHeight() {
			// fixes the problem described here: https://bugs.eclipse.org/bugs/show_bug.cgi?id=223015			
			return buttonHeight;
		}

		@Override
		protected void checkSubclass() {
			// allow subclassing
		}
	}

}
