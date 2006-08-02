/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * About Window
 *
 * @author anton
 */
public class GettingStartedWindow {

	private Shell shell;  //  @jve:decl-index=0:visual-constraint="10,10"
	private int activePage = 1;
	private Text gettingStartedText;
	private Button closeButton;
	private Button nextButton;

	public GettingStartedWindow() {
		createShell();		
	}
	
	public void open() {
		shell.open();
		Display display = Display.getCurrent();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		shell.dispose();
	}

	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getInstance().getString("title.gettingStarted"));
		shell.setSize(new Point(400, 240));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setBounds(10, 10, 0, 0);
		
		if (parent != null) {
			iconLabel.setImage(parent.getImage());
			shell.setImage(parent.getImage());
		}		
		iconLabel.pack();
		
		gettingStartedText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		gettingStartedText.setBounds(60, 10, 320, 160);
		gettingStartedText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		closeButton = new Button(shell, SWT.NONE);
		closeButton.setText(Labels.getInstance().getString("button.close"));
		closeButton.setBounds(110, 180, 80, 22);
		closeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
				shell.dispose();
			}
		});

		nextButton = new Button(shell, SWT.NONE);
		nextButton.setText(Labels.getInstance().getString("button.next"));
		nextButton.setBounds(210, 180, 80, 22);
		nextButton.setFocus();
		nextButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				activePage++;
				displayActivePage();
			}
		});
		
		shell.setDefaultButton(nextButton);
		
		displayActivePage();
	}
	
	private void displayActivePage() {
		String text = Labels.getInstance().getString("text.gettingStarted" + activePage);
		gettingStartedText.setText(text);
		
		// check for the next one
		try {
			Labels.getInstance().getString("text.gettingStarted" + (activePage+1));
		}
		catch (Exception e) {
			// no label, disable the next button
			nextButton.setEnabled(false);
			shell.setDefaultButton(closeButton);
			closeButton.setFocus();
		}
	}
	
}
