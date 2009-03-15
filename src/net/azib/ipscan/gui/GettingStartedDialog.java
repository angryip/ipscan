/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.platform.SWTHelper;

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
 * @author Anton Keks
 */
public class GettingStartedDialog extends AbstractModalDialog {

	private int activePage = 1;
	private Text gettingStartedText;
	private Button closeButton;
	private Button nextButton;

	@Override
	protected void populateShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getLabel("title.gettingStarted"));
		shell.setSize(new Point(400, 240));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		
		if (parent != null) {
			iconLabel.setImage(parent.getImage());
			shell.setImage(parent.getImage());
		}		
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 20;
				
		closeButton = new Button(shell, SWT.NONE);
		closeButton.setText(Labels.getLabel("button.close"));

		nextButton = new Button(shell, SWT.NONE);
		nextButton.setText(Labels.getLabel("button.next"));
		nextButton.setFocus();
		
		SWTHelper.setStockIconFor(closeButton, SWT.ABORT);
		SWTHelper.setStockIconFor(nextButton, SWT.ARROW_RIGHT);
		positionButtons(nextButton, closeButton);
		
		gettingStartedText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		gettingStartedText.setBounds(leftBound, 10, shell.getClientArea().width - leftBound - 10, closeButton.getLocation().y - 20);
		gettingStartedText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		closeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
				shell.dispose();
			}
		});
		nextButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				activePage++;
				displayActivePage();
			}
		});

		displayActivePage();
	}
	
	void displayActivePage() {
		String text = Labels.getLabel("text.gettingStarted" + activePage);
		gettingStartedText.setText(text);
		
		// check for the next one
		try {
			Labels.getLabel("text.gettingStarted" + (activePage+1));
		}
		catch (Exception e) {
			// no label, disable the next button
			nextButton.setEnabled(false);
			shell.setDefaultButton(closeButton);
			closeButton.setFocus();
		}
	}
	
}
