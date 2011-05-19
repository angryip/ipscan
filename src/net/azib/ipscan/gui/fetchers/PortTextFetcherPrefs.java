/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;
import net.azib.ipscan.fetchers.PortTextFetcher;
import net.azib.ipscan.gui.AbstractModalDialog;
import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * PortTextFetcherPrefs
 *
 * @author Anton Keks
 */
public class PortTextFetcherPrefs extends AbstractModalDialog implements FetcherPrefs {
	
	private PortTextFetcher fetcher;
	
	public PortTextFetcherPrefs() {
	}
	
	public void openFor(Fetcher fetcher) {
		this.fetcher = (PortTextFetcher) fetcher;
		open();
	}

	@Override
	protected void populateShell() {
		shell = new Shell(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM);
		shell.setText(fetcher.getName());
		shell.setLayout(LayoutHelper.formLayout(10, 10, 5));
		
		Combo predefinedCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		predefinedCombo.add(Labels.getLabel("fetcher.portText.custom"));
		//predefinedCombo.add("Web detect");
		//predefinedCombo.add("SMTP detect");
		predefinedCombo.select(0);
		predefinedCombo.setLayoutData(LayoutHelper.formData(null, new FormAttachment(100), new FormAttachment(0), null));
		
		Label sendLabel = new Label(shell, SWT.NONE);
		sendLabel.setText(Labels.getLabel("text.fetcher.portText.send"));
		sendLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, null, new FormAttachment(predefinedCombo, 0, SWT.BOTTOM)));
		Text sendText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		sendText.setText(stringToText(fetcher.getTextToSend()));
		sendText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(sendLabel), null));
		
		Label matchLabel = new Label(shell, SWT.NONE);
		matchLabel.setText(Labels.getLabel("text.fetcher.portText.match"));
		matchLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(sendText), null));
		Text matchText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		matchText.setText(fetcher.getMatchingRegexp().pattern());
		matchText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(sendText, 0, SWT.RIGHT), new FormAttachment(matchLabel), null));

		Label replaceLabel = new Label(shell, SWT.NONE);
		replaceLabel.setText(Labels.getLabel("text.fetcher.portText.replace"));
		replaceLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(matchText), null));
		Text replaceText = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		replaceText.setText("$1");
		replaceText.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(sendText, 0, SWT.RIGHT), new FormAttachment(replaceLabel), null));

		shell.pack();
	}

	/**
	 * @return
	 */
	private String stringToText(String s) {
		StringBuilder t = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '\n') 
				t.append("\\n");
			else if (c == '\r')
				t.append("\\r");
			else if (c == '\t')
				t.append("\\t");
			else
				t.append(c);
		}
		return t.toString();
	}
}
