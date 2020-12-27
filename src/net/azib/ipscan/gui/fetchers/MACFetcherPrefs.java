/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;
import net.azib.ipscan.fetchers.MACFetcher;
import net.azib.ipscan.gui.AbstractModalDialog;
import net.azib.ipscan.gui.util.LayoutHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.*;

public class MACFetcherPrefs extends AbstractModalDialog implements FetcherPrefs {
	private MACFetcher fetcher;
	private Text separator;

	public void openFor(Fetcher fetcher) {
		this.fetcher = (MACFetcher) fetcher;
		open();
	}

	@Override
	protected void populateShell() {
		shell = new Shell(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM);
		shell.setText(fetcher.getName());
		shell.setLayout(LayoutHelper.formLayout(10, 10, 5));

		Label separatorLabel = new Label(shell, SWT.NONE);
		separatorLabel.setText(Labels.getLabel("fetcher.mac.separator"));
		separator = new Text(shell, SWT.BORDER);
		separator.setText(fetcher.getSeparator());
		separator.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(separatorLabel), null));

		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));

		positionButtonsInFormLayout(okButton, cancelButton, separator);

		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				savePreferences();
				shell.close();
			}
		});
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		shell.pack();
	}

	void savePreferences() {
		String text = separator.getText();
		fetcher.setSeparator(text);
		fetcher.getPreferences().put("separator", text);
	}
}
