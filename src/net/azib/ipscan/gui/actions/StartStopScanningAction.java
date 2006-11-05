/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.net.InetAddress;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScannerThread;
import net.azib.ipscan.core.ScannerThreadFactory;
import net.azib.ipscan.core.ScanningStateCallback;
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
public class StartStopScanningAction implements SelectionListener, ScanningStateCallback {
	
	private ScannerThreadFactory scannerThreadFactory;
	private ScannerThread scannerThread;

	private StatusBar statusBar;
	private ResultTable resultTable;
	private FeederGUIRegistry feederRegistry;
	private Button button;
	private Image[] images = new Image[4];
	
	private Display display;
	
	private int state = ScanningStateCallback.STATE_IDLE;
	
	public StartStopScanningAction(ScannerThreadFactory scannerThreadFactory, ResultTable resultTable, StatusBar statusBar, FeederGUIRegistry feederRegistry, Button startStopButton) {
		this.scannerThreadFactory = scannerThreadFactory;
		this.resultTable = resultTable;
		this.statusBar = statusBar;
		this.feederRegistry = feederRegistry;
		this.button = startStopButton;
		this.display = button.getDisplay();
		
		// pre-load button images
		images[ScanningStateCallback.STATE_IDLE] = new Image(null, Labels.getInstance().getImageAsStream("button.start.img"));
		images[ScanningStateCallback.STATE_SCANNING] = new Image(null, Labels.getInstance().getImageAsStream("button.stop.img"));
		images[ScanningStateCallback.STATE_STOPPING] = new Image(null, Labels.getInstance().getImageAsStream("button.kill.img"));
		images[ScanningStateCallback.STATE_KILLING] = images[ScanningStateCallback.STATE_STOPPING];
		
		// set the defaultimage
		button.setImage(images[state]);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

	public void widgetSelected(SelectionEvent e) {
		switch (state) {
			case ScanningStateCallback.STATE_IDLE:
				// start the scan!
				resultTable.initNewScan(feederRegistry.current().getInfo());
				ScanningResultsConsumer resultsConsumer = new ScanningResultsConsumer(resultTable);
				scannerThread = scannerThreadFactory.createScannerThread(feederRegistry.current().getFeeder());
				scannerThread.setResultsCallback(resultsConsumer);
				scannerThread.setStatusCallback(this);
				scannerThread.start();
				break;
			case ScanningStateCallback.STATE_SCANNING:
				scannerThread.forceStop();
				break;
			case ScanningStateCallback.STATE_STOPPING:
				scannerThread.abort();
				break;
			case ScanningStateCallback.STATE_KILLING:
				break;
		}
	}
	
	public void scannerStateChanged(int status) {
		this.state = status;
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				if (statusBar.isDisposed())
					return;
				
				switch (StartStopScanningAction.this.state) {
					case STATE_IDLE:
						// reset state text
						statusBar.setStatusText(null);
						statusBar.setProgress(0);
						break;
					case STATE_STOPPING:
						statusBar.setStatusText(Labels.getLabel("state.waitForThreads"));
						break;
					case STATE_KILLING:
						statusBar.setStatusText(Labels.getLabel("state.killingThreads"));
						break;
				}
				// change button image
				button.setImage(images[StartStopScanningAction.this.state]);
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
				button.setImage(images[StartStopScanningAction.this.state]);
			}
		});
	}
	
}
