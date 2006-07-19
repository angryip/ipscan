/**
 * 
 */
package net.azib.ipscan.gui.actions;

import java.net.InetAddress;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScannerThread;
import net.azib.ipscan.core.ScanningStateCallback;
import net.azib.ipscan.gui.MainWindow;
import net.azib.ipscan.gui.ScanningResultsConsumer;

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
	
	private ScannerThread scannerThread;
	
	private MainWindow mainWindow;
	private Button button;
	private Image[] images = new Image[4];
	
	private Display display;
	
	private int state = ScanningStateCallback.STATE_IDLE;
	
	public StartStopScanningAction(MainWindow mainWindow, Button button) {
		this.mainWindow = mainWindow;
		this.button = button;
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
				mainWindow.getResultTable().initNewScan(mainWindow.getFeederGUI().getInfo());
				ScanningResultsConsumer resultsConsumer = new ScanningResultsConsumer(mainWindow.getResultTable());
				scannerThread = new ScannerThread(mainWindow.getFeederGUI().getFeeder(), mainWindow.getResultTable().getFetchers());
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
				if (mainWindow.isDisposed())
					return;
				
				switch (StartStopScanningAction.this.state) {
					case STATE_IDLE:
						// reset state text
						mainWindow.setStatusText(null);
						mainWindow.setProgress(0);
						break;
					case STATE_STOPPING:
						mainWindow.setStatusText(Labels.getInstance().getString("state.waitForThreads"));
						break;
					case STATE_KILLING:
						mainWindow.setStatusText(Labels.getInstance().getString("state.killingThreads"));
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
				if (mainWindow.isDisposed())
					return;
				
				if (currentAddress != null) {
					mainWindow.setStatusText(Labels.getInstance().getString("state.scanning") + currentAddress.getHostAddress());
				}					

				mainWindow.setRunningThreads(runningThreads);
				mainWindow.setProgress(percentageComplete);

				// change button image
				button.setImage(images[StartStopScanningAction.this.state]);
			}
		});
	}
	
}
