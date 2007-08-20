/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.ConfigDetector;
import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * ConfigDetectorDialog - a GUI for {@link ConfigDetector}
 *
 * @author Anton Keks
 */
public class ConfigDetectorDialog extends AbstractModalDialog implements ConfigDetector.DetectorCallback {
	
	private GlobalConfig config;
	private ConfigDetector configDetector;
	private ProgressBar tryProgressBar;
	private int tryCount;
	private ProgressBar successProgressBar;
	private int successCount;
	private Button startButton;
	private Button closeButton;
	private Label tryCountLabel;
	private Label successCountLabel;
	
	public ConfigDetectorDialog(GlobalConfig config, ConfigDetector configDetector) {
		this.config = config;
		this.configDetector = configDetector;
		this.configDetector.setCallback(this);
	}
	
	@Override
	public void open() {
		createShell();
		super.open();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setLayout(LayoutHelper.formLayout(10, 10, 10));

		shell.setText(Labels.getLabel("title.configDetect"));
		
		Label infoLabel = new Label(shell, SWT.WRAP);
		infoLabel.setText((config.isFirstRun ? Labels.getLabel("text.configDetect.firstRun") : "") + Labels.getLabel("text.configDetect"));
		infoLabel.setLayoutData(LayoutHelper.formData(340, SWT.DEFAULT, new FormAttachment(0), new FormAttachment(100), new FormAttachment(0), null));
		
		Label tryLabel = new Label(shell, SWT.NONE);
		tryLabel.setText(Labels.getLabel("text.configDetect.tries"));
		tryLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(infoLabel, 10), null));
		tryCountLabel = new Label(shell, SWT.NONE);
		tryCountLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(tryLabel, -5), new FormAttachment(100), new FormAttachment(infoLabel, 10), null));
		tryProgressBar = new ProgressBar(shell, SWT.NONE);
		tryProgressBar.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(tryLabel), null));
		
		Label successLabel = new Label(shell, SWT.NONE);
		successLabel.setText(Labels.getLabel("text.configDetect.successes"));
		successLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(tryProgressBar, 10), null));
		successCountLabel = new Label(shell, SWT.NONE);
		successCountLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(successLabel, -5), new FormAttachment(100), new FormAttachment(tryProgressBar, 10), null));
		successProgressBar = new ProgressBar(shell, SWT.NONE);
		successProgressBar.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(successLabel), null));
		
		startButton = new Button(shell, SWT.NONE);
		startButton.setText(Labels.getLabel("button.start"));
		startButton.addListener(SWT.Selection, new StartButtonListener());
		
		closeButton = new Button(shell, SWT.NONE);
		closeButton.setText(Labels.getLabel("button.close"));
		closeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
			}
		});
		
		positionButtonsInFormLayout(startButton, closeButton, successProgressBar);
		
		shell.pack();
	}

	public void onDetectorTry() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tryCount++;
				tryCountLabel.setText(Integer.toString(tryCount));
				tryProgressBar.setSelection(tryCount);
			}
		});
	}

	public void onDetectorSuccess() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				successCount++;
				successCountLabel.setText(Integer.toString(successCount));
				successProgressBar.setSelection(successCount);
			}
		});
	}

	private void onStart() {
		startButton.setEnabled(false);
		closeButton.setEnabled(false);
		tryCount = 0;
		tryCountLabel.setText("0");
		tryProgressBar.setMaximum(config.maxThreads);
		tryProgressBar.setSelection(0);
		successCount = 0;
		successCountLabel.setText("0");
		successProgressBar.setMaximum(config.maxThreads);
		successProgressBar.setSelection(0);
	}

	private void onFinish() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				startButton.setEnabled(true);
				closeButton.setEnabled(true);
				
				// ask the user if they want to use the detected value
				if (config.isFirstRun || config.maxThreads != successCount) {
					MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);
					box.setText(Labels.getLabel("title.configDetect"));
					box.setMessage(String.format(Labels.getLabel("text.configDetect.setMaxThreads"), successCount) +
								   (config.isFirstRun ? Labels.getLabel("text.configDetect.setMaxThreads.firstRun") : ""));
					if (box.open() == SWT.YES) {
						config.maxThreads = successCount;
						shell.close();
					}
				}
			}
		});
	}
	
	private class StartButtonListener implements Listener {
		public void handleEvent(Event event) {
			onStart();
			// start detection in separate thread
			new Thread() {
				public void run() {
					configDetector.detectMaxThreads();
					onFinish();
				}
			}.start();
		}
	}

}
