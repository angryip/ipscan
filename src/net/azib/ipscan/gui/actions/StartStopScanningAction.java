/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.net.InetAddress;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScannerThread;
import net.azib.ipscan.core.ScannerThreadFactory;
import net.azib.ipscan.core.ScanningProgressCallback;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.ResultTable;
import net.azib.ipscan.gui.ScanningResultsConsumer;
import net.azib.ipscan.gui.StatusBar;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

/**
 * Start/Stop button action class.
 * It listens to presses on the buttons as well as updates gui statuses
 * 
 * @author anton
 */
public class StartStopScanningAction implements SelectionListener, ScanningProgressCallback, StateTransitionListener {
	
	private ScannerThreadFactory scannerThreadFactory;
	private ScannerThread scannerThread;

	private StatusBar statusBar;
	private ResultTable resultTable;
	private FeederGUIRegistry feederRegistry;
	private Button button;
	private Image[] buttonImages = new Image[ScanningState.values().length];
	private String[] buttonTexts = new String[ScanningState.values().length];
	
	private Display display;
	
	private StateMachine stateMachine;
	
	public StartStopScanningAction(ScannerThreadFactory scannerThreadFactory, StateMachine stateMachine, ResultTable resultTable, StatusBar statusBar, FeederGUIRegistry feederRegistry, Button startStopButton) {
		this.scannerThreadFactory = scannerThreadFactory;
		this.resultTable = resultTable;
		this.statusBar = statusBar;
		this.feederRegistry = feederRegistry;
		this.button = startStopButton;
		this.display = button.getDisplay();
		this.stateMachine = stateMachine;
		
		// pre-load button images
		buttonImages[ScanningState.IDLE.ordinal()] = new Image(null, Labels.getInstance().getImageAsStream("button.start.img"));
		buttonImages[ScanningState.SCANNING.ordinal()] = new Image(null, Labels.getInstance().getImageAsStream("button.stop.img"));
		buttonImages[ScanningState.STOPPING.ordinal()] = new Image(null, Labels.getInstance().getImageAsStream("button.kill.img"));
		buttonImages[ScanningState.KILLING.ordinal()] = buttonImages[ScanningState.STOPPING.ordinal()];
		
		// pre-load button texts
		buttonTexts[ScanningState.IDLE.ordinal()] = Labels.getLabel("button.start");
		buttonTexts[ScanningState.SCANNING.ordinal()] = Labels.getLabel("button.stop");
		buttonTexts[ScanningState.STOPPING.ordinal()] = Labels.getLabel("button.kill");
		buttonTexts[ScanningState.KILLING.ordinal()] = Labels.getLabel("button.kill");

		// add listeners to all state changes
		for (ScanningState state : ScanningState.values()) {
			state.addTransitionListener(this);
		}
		
		// set the defaultimage
		ScanningState state = stateMachine.getState();
		button.setImage(buttonImages[state.ordinal()]);
		button.setText(buttonTexts[state.ordinal()]);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		stateMachine.transitionToNext();
	}
	
	public void transitionTo(final ScanningState state) {
		if (display.isDisposed())
			return;
		display.syncExec(new Runnable() {
			public void run() {
				if (statusBar.isDisposed())
					return;
				
				switch (state) {
					case IDLE:
						// reset state text
						statusBar.setStatusText(null);
						statusBar.setProgress(0);
						break;
					case SCANNING:
						// start the scan!
						resultTable.initNewScan(feederRegistry.current().getInfo());
						ScanningResultsConsumer resultsConsumer = new ScanningResultsConsumer(resultTable);
						scannerThread = scannerThreadFactory.createScannerThread(feederRegistry.current().getFeeder(), StartStopScanningAction.this);
						// TODO: fix this: this is needed here to avoid cylic dependencies...
						scannerThread.setResultsCallback(resultsConsumer);
						scannerThread.start();
						break;
					case STOPPING:
						statusBar.setStatusText(Labels.getLabel("state.waitForThreads"));
						break;
					case KILLING:
						statusBar.setStatusText(Labels.getLabel("state.killingThreads"));
						break;
				}
				// change button image
				button.setImage(buttonImages[state.ordinal()]);
				button.setText(buttonTexts[state.ordinal()]);
			}
		});
	}

	public void updateProgress(final InetAddress currentAddress, final int runningThreads, final int percentageComplete) {
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				if (statusBar.isDisposed())
					return;
				
				if (currentAddress != null) {
					statusBar.setStatusText(Labels.getLabel("state.scanning") + currentAddress.getHostAddress());
				}					

				statusBar.setRunningThreads(runningThreads);
				statusBar.setProgress(percentageComplete);

				// change button image
				button.setImage(buttonImages[stateMachine.getState().ordinal()]);
			}
		});
	}

}
