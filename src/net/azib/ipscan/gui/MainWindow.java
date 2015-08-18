/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.actions.StartStopScanningAction;
import net.azib.ipscan.gui.actions.ToolsActions;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.menu.ResultsContextMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;
import javax.inject.Named;

import static net.azib.ipscan.gui.util.LayoutHelper.formData;

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
	
	private Button startStopButton;
	private Combo feederSelectionCombo;
	private FeederGUIRegistry feederRegistry;
	
	private StatusBar statusBar;
	private ToolBar prefsButton;
	private ToolBar fetchersButton;

	/**
	 * Creates and initializes the main window.
	 */
	@Inject
	public MainWindow(Shell shell, GUIConfig guiConfig, @Named("feederArea") Composite feederArea,
					  @Named("controlsArea") Composite controlsArea, @Named("feederSelectionCombo") Combo feederSelectionCombo,
					  @Named("startStopButton") Button startStopButton, StartStopScanningAction startStopScanningAction,
					  ResultTable resultTable, StatusBar statusBar, ResultsContextMenu resultsContextMenu,
					  FeederGUIRegistry feederGUIRegistry, final StateMachine stateMachine,
					  ToolsActions.Preferences preferencesListener, ToolsActions.ChooseFetchers chooseFetchersListener,
					  MainMenu menuBar /* don't delete: initiates main menu creation */ ) {

		this.shell = shell;
		this.guiConfig = guiConfig;
		this.statusBar = statusBar;

		initShell(shell);
		initFeederArea(feederArea, feederGUIRegistry);
		initControlsArea(controlsArea, feederSelectionCombo, startStopButton, startStopScanningAction, preferencesListener, chooseFetchersListener);
		initTableAndStatusBar(resultTable, resultsContextMenu, statusBar);

		// after all controls are initialized, resize and open
		shell.setSize(guiConfig.getMainWindowSize());
		shell.open();
		if (guiConfig.isMainWindowMaximized) {
			shell.setMaximized(true);
		}

		if (guiConfig.isFirstRun) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					GettingStartedDialog dialog = new GettingStartedDialog();
					if (Platform.CRIPPLED_WINDOWS)
						dialog.prependText(Labels.getLabel("text.crippledWindowsInfo"));
					if (Platform.GNU_JAVA)
						dialog.prependText(Labels.getLabel("text.gnuJavaInfo"));

					MainWindow.this.shell.forceActive();
					dialog.open();
					MainWindow.this.guiConfig.isFirstRun = false;
				}
			});
		}

		stateMachine.addTransitionListener(new EnablerDisabler());

		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				// asynchronously run init handlers outside of the constructor
				stateMachine.init();
			}
		});
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
				guiConfig.setMainWindowSize(shell.getSize(), shell.getMaximized());
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
	private void initTableAndStatusBar(ResultTable resultTable, Menu resultsContextMenu, StatusBar statusBar) {
		resultTable.setLayoutData(formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(feederArea), new FormAttachment(statusBar.getComposite())));
		resultTable.setMenu(resultsContextMenu);
	}

	private void initFeederArea(Composite feederArea, FeederGUIRegistry feederRegistry) {
		// feederArea is the placeholder for the visible feeder
		this.feederArea = feederArea;
		feederArea.setLayoutData(formData(new FormAttachment(0), null, new FormAttachment(0), null));
		this.feederRegistry = feederRegistry;
	}

	/**
	 * This method initializes main controls of the main window	
	 */
	private void initControlsArea(final Composite controlsArea, final Combo feederSelectionCombo, final Button startStopButton, final StartStopScanningAction startStopScanningAction, final ToolsActions.Preferences preferencesListener, final ToolsActions.ChooseFetchers chooseFetchersListsner) {
		controlsArea.setLayoutData(formData(new FormAttachment(feederArea), null, new FormAttachment(0), new FormAttachment(feederArea, 0, SWT.BOTTOM)));

		// start/stop button
		this.startStopButton = startStopButton;
		shell.setDefaultButton(startStopButton);
		startStopButton.addSelectionListener(startStopScanningAction);
		startStopButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// feeder selection combobox
		this.feederSelectionCombo = feederSelectionCombo;
		feederSelectionCombo.pack();
		IPFeederSelectionListener feederSelectionListener = new IPFeederSelectionListener();
		feederSelectionCombo.addSelectionListener(feederSelectionListener);
		// initialize the selected feeder GUI 
		feederSelectionCombo.select(guiConfig.activeFeeder);
		feederSelectionCombo.setToolTipText(Labels.getLabel("combobox.feeder.tooltip"));

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
		boolean twoRowToolbar = feederRegistry.current().getSize().y > startStopButton.getSize().y * 1.5;

		if (twoRowToolbar) {
			GridLayout layout = new GridLayout(2, false);
			layout.marginTop = -2;
			layout.marginBottom = 0;
			layout.verticalSpacing = 3;
			startStopButton.getParent().setLayout(layout);

			prefsButton.moveAbove(startStopButton);
		}
		else {
			startStopButton.getParent().setLayout(new GridLayout(4, false));
			startStopButton.moveAbove(prefsButton);
		}
	}
			
	/**
	 * IP Feeder selection listener. Updates the GUI according to the IP Feeder selection.
	 */
	class IPFeederSelectionListener extends SelectionAdapter {
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
		public void transitionTo(final ScanningState state, Transition transition) {
			if (transition != Transition.START && transition != Transition.COMPLETE)
				return;
			
			boolean enabled = state == ScanningState.IDLE;
			feederArea.setEnabled(enabled);
			feederSelectionCombo.setEnabled(enabled);
			prefsButton.setEnabled(enabled);
			fetchersButton.setEnabled(enabled);
			statusBar.setEnabled(enabled);
		}
	}
}
