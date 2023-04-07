/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.GUIConfig;
import net.azib.ipscan.config.GUIConfig.DisplayMethod;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.*;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import java.net.InetAddress;

import static net.azib.ipscan.core.state.ScanningState.*;
import static net.azib.ipscan.gui.util.LayoutHelper.icon;

/**
 * Start/Stop button action class.
 * It listens to presses on the buttons as well as updates gui statuses
 * 
 * @author Anton Keks
 */
public class StartStopScanningAction implements SelectionListener, ScanningProgressCallback, StateTransitionListener {
	private ScannerDispatcherThreadFactory scannerThreadFactory;
	private ScannerDispatcherThread scannerThread;
	private GUIConfig guiConfig;
	private PingerRegistry pingerRegistry;

	private String mainWindowTitle;
	private StatusBar statusBar;
	private TaskItem taskBarItem;
	private ResultTable resultTable;
	private FeederGUIRegistry feederRegistry;
	private Button button;
	private AbstractFeederGUI abstractFeederGUI;
	
	Image[] buttonImages = new Image[ScanningState.values().length];
	String[] buttonTexts = new String[ScanningState.values().length];
	
	private Display display;
	private StateMachine stateMachine;
	
	/**
	 * Creates internal stuff independent from all other external dependencies
	 */
	StartStopScanningAction(Display display) {
		this.display = display;
		
		// preload button images
		buttonImages[IDLE.ordinal()] = icon("buttons/start");
		buttonImages[SCANNING.ordinal()] = icon("buttons/stop");
		buttonImages[STARTING.ordinal()] = buttonImages[SCANNING.ordinal()];
		buttonImages[RESTARTING.ordinal()] = buttonImages[SCANNING.ordinal()];
		buttonImages[STOPPING.ordinal()] = icon("buttons/kill");
		buttonImages[KILLING.ordinal()] = buttonImages[STOPPING.ordinal()];
		
		// preload button texts
		buttonTexts[IDLE.ordinal()] = Labels.getLabel("button.start");
		buttonTexts[SCANNING.ordinal()] = Labels.getLabel("button.stop");
		buttonTexts[STARTING.ordinal()] = buttonTexts[SCANNING.ordinal()];
		buttonTexts[RESTARTING.ordinal()] = buttonTexts[SCANNING.ordinal()];
		buttonTexts[STOPPING.ordinal()] = Labels.getLabel("button.kill");
		buttonTexts[KILLING.ordinal()] = Labels.getLabel("button.kill");
	}

	public StartStopScanningAction(ScannerDispatcherThreadFactory scannerThreadFactory, StateMachine stateMachine, ResultTable resultTable,
								   StatusBar statusBar, FeederGUIRegistry feederRegistry, PingerRegistry pingerRegistry,
								   Button startStopButton, GUIConfig guiConfig, AbstractFeederGUI abstractFeederGUI) {
		this(startStopButton.getDisplay());

		this.scannerThreadFactory = scannerThreadFactory;
		this.resultTable = resultTable;
		this.statusBar = statusBar;
		this.feederRegistry = feederRegistry;
		this.pingerRegistry = pingerRegistry;
		this.button = startStopButton;
		this.stateMachine = stateMachine;
		this.guiConfig = guiConfig;
		this.taskBarItem = getTaskBarItem();
		this.abstractFeederGUI = abstractFeederGUI;
		
		// add listeners to all state changes
		stateMachine.addTransitionListener(this);
		
		// set the default image
		ScanningState state = stateMachine.getCurrentState();
		button.setImage(buttonImages[state.ordinal()]);
		button.setText(buttonTexts[state.ordinal()]);
	}

	private TaskItem getTaskBarItem() {
		TaskBar bar = display.getSystemTaskBar();
		// TODO: test on Mac and re-enable this feature (see issue #82)
		if (bar == null || Platform.MAC_OS) return null;
		TaskItem item = bar.getItem(statusBar.getShell());
		if (item == null) item = bar.getItem(null);
		if (item != null) item.setProgressState(SWT.NORMAL);
		return item;
	}

	/**
	 * Called when scanning button is clicked
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	/**
	 * Called when scanning button is clicked
	 */
	public void widgetSelected(SelectionEvent event) {
		// ask for confirmation before erasing scanning results
		if (stateMachine.inCurrentState(IDLE)) {
			if (!preScanChecks())
				return;
		}
		stateMachine.transitionToNext();
	}

	private boolean preScanChecks() {
		// ask user for confirmation if needed
		if (guiConfig.askScanConfirmation && resultTable.getItemCount() > 0) {
			MessageBox box = new MessageBox(resultTable.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.SHEET);
			box.setText(Labels.getLabel("text.scan.new"));
			box.setMessage(Labels.getLabel("text.scan.confirmation"));
			return box.open() == SWT.YES;
		}
		return true;
	}
	
	public void transitionTo(final ScanningState state, final Transition transition) {
		if (statusBar.isDisposed() || transition == Transition.INIT)
			return;
		
		// TODO: separate GUI and non-GUI stuff
		switch (state) {
			case IDLE:
				// reset state text
				button.setEnabled(true);
				updateProgress(null, 0, 0);
				statusBar.setStatusText(null);
				break;
			case STARTING:
				// start the scan from scratch!
				if (transition != Transition.CONTINUE)
					resultTable.removeAll();

				try {
					scannerThread = scannerThreadFactory.createScannerThread(abstractFeederGUI.createFeeder(), StartStopScanningAction.this, createResultsCallback(state));
					stateMachine.startScanning();
					mainWindowTitle = statusBar.getShell().getText();
				}
				catch (RuntimeException e) {
					stateMachine.reset();
					throw e;
				}
				break;
			case RESTARTING:
				// restart the scanning - rescan
				resultTable.resetSelection();
				try {
					scannerThread = scannerThreadFactory.createScannerThread(feederRegistry.createRescanFeeder(resultTable.getSelection()), StartStopScanningAction.this, createResultsCallback(state));
					stateMachine.startScanning();
					mainWindowTitle = statusBar.getShell().getText();
				}
				catch (RuntimeException e) {
					stateMachine.reset();
					throw e;
				}
				break;
			case SCANNING:
				scannerThread.start();
				break;
			case STOPPING:
				statusBar.setStatusText(Labels.getLabel("state.waitForThreads"));
				break;
			case KILLING:
				button.setEnabled(false);
				statusBar.setStatusText(Labels.getLabel("state.killingThreads"));
				break;
		}
		button.setImage(buttonImages[state.ordinal()]);
		button.setText(buttonTexts[state.ordinal()]);
	}
	
	/**
	 * @return the appropriate ResultsCallback instance, depending on the configured display method.
	 */
	private ScanningResultCallback createResultsCallback(ScanningState state) {
		// rescanning must follow the same strategy of displaying all hosts (even the dead ones), because the results are already in the list
		if (guiConfig.displayMethod == DisplayMethod.ALL || state == RESTARTING) {
			return new ScanningResultCallback() {
				public void prepareForResults(ScanningResult result) {
					resultTable.addOrUpdateResultRow(result);
				}
				public void consumeResults(ScanningResult result) {
					resultTable.addOrUpdateResultRow(result);
				}
			};
		}
		if (guiConfig.displayMethod == DisplayMethod.ALIVE) {
			return new ScanningResultCallback() {
				public void prepareForResults(ScanningResult result) {
				}
				public void consumeResults(ScanningResult result) {
					if (result.getType().ordinal() >= ResultType.ALIVE.ordinal())
						resultTable.addOrUpdateResultRow(result);
				}
			};
		}
		if (guiConfig.displayMethod == DisplayMethod.PORTS) {
			return new ScanningResultCallback() {
				public void prepareForResults(ScanningResult result) {
				}
				public void consumeResults(ScanningResult result) {
					if (result.getType() == ResultType.WITH_PORTS)
						resultTable.addOrUpdateResultRow(result);
				}
			};
		}
		throw new UnsupportedOperationException(guiConfig.displayMethod.toString());
	}

	public void updateProgress(final InetAddress currentAddress, final int runningThreads, final int percentageComplete) {
		if (display.isDisposed()) return;
		display.asyncExec(() -> {
			if (statusBar.isDisposed()) return;

			// update status bar
			if (currentAddress != null) {
				statusBar.setStatusText(Labels.getLabel("state.scanning") + currentAddress.getHostAddress());
			}
			statusBar.setRunningThreads(runningThreads);
			statusBar.setProgress(percentageComplete);
			if (taskBarItem != null) taskBarItem.setProgress(percentageComplete);

			// show percentage in main window title
			if (!stateMachine.inCurrentState(IDLE))
				statusBar.getShell().setText(percentageComplete + "% - " + mainWindowTitle);
			else
				statusBar.getShell().setText(mainWindowTitle);

			// change button image according to the current state
			button.setImage(buttonImages[stateMachine.getCurrentState().ordinal()]);
		});
	}

}
