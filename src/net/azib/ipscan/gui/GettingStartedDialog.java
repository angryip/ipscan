package net.azib.ipscan.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

import static net.azib.ipscan.config.Config.getConfig;
import static net.azib.ipscan.config.Labels.getLabel;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

public class GettingStartedDialog extends AbstractModalDialog {
	private int activePage;
	private List<String> texts = new ArrayList<>();
	private Text gettingStartedText;
	private Button allowReports;
	private Button closeButton;
	private Button nextButton;

	public GettingStartedDialog() {
		int num = 1;
		try {
			while (true) {
				texts.add(getLabel("text.gettingStarted" + num++));
			}
		}
		catch (Exception noMoreTexts) {}
	}

	GettingStartedDialog prependText(String text) {
		texts.add(0, text);
		return this;
	}

	@Override
	protected void populateShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(getLabel("title.gettingStarted"));
		shell.setSize(new Point(600, 300));

		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		
		if (parent != null) {
			iconLabel.setImage(parent.getImage());
			shell.setImage(parent.getImage());
		}		
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 20;

		allowReports = new Button(shell, SWT.CHECK);
		allowReports.setText(getLabel("preferences.allowReports"));
		allowReports.pack();
		allowReports.setSelection(getConfig().allowReports);
		allowReports.addSelectionListener(widgetSelectedAdapter(e -> getConfig().allowReports = allowReports.getSelection()));

		closeButton = new Button(shell, SWT.NONE);
		closeButton.setText(getLabel("button.close"));

		nextButton = new Button(shell, SWT.NONE);
		nextButton.setText(getLabel("button.next"));
		nextButton.setFocus();
		
		positionButtons(nextButton, closeButton);
		allowReports.setBounds(leftBound, nextButton.getBounds().y, allowReports.getSize().x, nextButton.getBounds().height);
		
		gettingStartedText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		gettingStartedText.setBounds(leftBound, 10, shell.getClientArea().width - leftBound - 10, closeButton.getLocation().y - 20);
		gettingStartedText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		
		closeButton.addListener(SWT.Selection, event -> {
			shell.close();
			shell.dispose();
		});
		nextButton.addListener(SWT.Selection, event -> displayActivePage());

		displayActivePage();
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.SHEET;
	}

	private void displayActivePage() {
		gettingStartedText.setText(texts.get(activePage++));
		
		if (activePage >= texts.size()) {
			nextButton.setEnabled(false);
			shell.setDefaultButton(closeButton);
			closeButton.setFocus();
		}
	}
	
}
