/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
import org.eclipse.swt.widgets.*;

import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;

public class PortTextFetcherPrefs extends AbstractModalDialog implements FetcherPrefs {
	private PortTextFetcher fetcher;
	private Text textToSend;
	private Text matchingRegexp;
	private Text extractGroup;

	public void openFor(Fetcher fetcher) {
		this.fetcher = (PortTextFetcher) fetcher;
		open();
	}

	@Override
	protected void populateShell() {
		shell = new Shell(Display.getCurrent().getActiveShell(), SWT.DIALOG_TRIM);
		shell.setText(fetcher.getName());
		shell.setLayout(LayoutHelper.formLayout(10, 10, 5));
		
		//Combo predefinedCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		//predefinedCombo.add(Labels.getLabel("fetcher.portText.custom"));
		//predefinedCombo.add("Web detect");
		//predefinedCombo.add("SMTP detect");
		//predefinedCombo.select(0);
		//predefinedCombo.setLayoutData(LayoutHelper.formData(null, new FormAttachment(100), new FormAttachment(0), null));
		
		Label sendLabel = new Label(shell, SWT.NONE);
		sendLabel.setText(Labels.getLabel("text.fetcher.portText.send"));
		//sendLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, null, new FormAttachment(predefinedCombo, 0, SWT.BOTTOM)));
		textToSend = new Text(shell, SWT.BORDER);
		textToSend.setText(toEditableText(fetcher.getTextToSend()));
		textToSend.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(100), new FormAttachment(sendLabel), null));
		
		Label matchLabel = new Label(shell, SWT.NONE);
		matchLabel.setText(Labels.getLabel("text.fetcher.portText.match"));
		matchLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(textToSend), null));
		matchingRegexp = new Text(shell, SWT.BORDER);
		matchingRegexp.setText(fetcher.getMatchingRegexp().pattern());
		matchingRegexp.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(textToSend, 0, SWT.RIGHT), new FormAttachment(matchLabel), null));

		Label replaceLabel = new Label(shell, SWT.NONE);
		replaceLabel.setText(Labels.getLabel("text.fetcher.portText.replace"));
		replaceLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(matchingRegexp), null));
		extractGroup = new Text(shell, SWT.BORDER);
		extractGroup.setText("$1");
		extractGroup.setLayoutData(LayoutHelper.formData(new FormAttachment(0), new FormAttachment(textToSend, 0, SWT.RIGHT), new FormAttachment(replaceLabel), null));

		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText(Labels.getLabel("button.OK"));

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(Labels.getLabel("button.cancel"));

		positionButtonsInFormLayout(okButton, cancelButton, extractGroup);

		okButton.addListener(SWT.Selection, e -> {
			savePreferences();
			close();
		});
		cancelButton.addListener(SWT.Selection, e -> close());

		shell.pack();
	}

	void savePreferences() {
		Preferences prefs = fetcher.getPreferences();

		String text = toRealText(textToSend.getText());
		fetcher.setTextToSend(text);
		prefs.put("textToSend", text);

		fetcher.setMatchingRegexp(Pattern.compile(matchingRegexp.getText()));
		prefs.put("matchingRegexp", fetcher.getMatchingRegexp().pattern());

		fetcher.setExtractGroup(parseInt(extractGroup.getText().replace("$", "")));
		prefs.putInt("extractGroup", fetcher.getExtractGroup());
	}

	static String toEditableText(String s) {
		StringBuilder t = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '\n') t.append("\\n");
			else if (c == '\r') t.append("\\r");
			else if (c == '\t') t.append("\\t");
			else if (c < 10) t.append("\\x0").append(toHexString(c).toUpperCase());
			else if (c < ' ') t.append("\\x").append(toHexString(c).toUpperCase());
			else t.append(c);
		}
		return t.toString();
	}

	static String toRealText(String s) {
		s = s.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
		if (s.contains("\\x")) {
			StringBuffer t = new StringBuffer();
			Matcher m = Pattern.compile("\\\\x(\\d{2})").matcher(s);
			while (m.find()) {
				m.appendReplacement(t, new String(new char[] {(char) parseInt(m.group(1), 16)}));
			}
			m.appendTail(t);
			s = t.toString();
		}
		return s;
	}
}
