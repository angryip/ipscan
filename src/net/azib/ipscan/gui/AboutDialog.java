package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.actions.BrowserLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import static net.azib.ipscan.config.Version.*;

public class AboutDialog extends AbstractModalDialog {
	public AboutDialog() {}

	@Override
	protected void populateShell() {
		shell.setText(Labels.getLabel("title.about"));
		shell.setSize(new Point(500, 393));

		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		if (shell.getImage() != null) {
			iconLabel.setImage(shell.getImage());
		}		
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 20;

		String aboutText = Labels.getLabel("text.about")
			.replace("%NAME", NAME)
			.replace("%VERSION", getVersion())
			.replace("%DATE", getBuildDate())
			.replace("%COPYLEFT", COPYLEFT);
		
		Label aboutLabel = new Label(shell, SWT.NONE);
		aboutLabel.setText(aboutText);
		aboutLabel.setLocation(leftBound, 10);
		aboutLabel.pack();
		
		Label websiteLabel = createLinkLabel(WEBSITE, WEBSITE);
		websiteLabel.setLocation(leftBound, 10 + aboutLabel.getBounds().height);

		String systemText = Labels.getLabel("text.about.system")
			.replace("%JAVA", System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"))
			.replace("%OS", System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")")
			.replace("%SWT", SWT.getPlatform() + " " + SWT.getVersion());

		Label systemLabel = new Label(shell, SWT.NONE);
		systemLabel.setText(systemText);
		systemLabel.setLocation(leftBound, 20 + aboutLabel.getBounds().height + websiteLabel.getBounds().height);
		systemLabel.pack();
		
		Button button = createCloseButton();
		
		Text licenseText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		licenseText.setBounds(leftBound, systemLabel.getBounds().y + systemLabel.getBounds().height + 10, 
							  shell.getClientArea().width - leftBound - 10, 
							  button.getLocation().y - systemLabel.getBounds().y - systemLabel.getBounds().height - 20);
		licenseText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		licenseText.setText("Licensed under the GNU General Public License Version 2\n\n" +
							NAME + " is free software; you can redistribute it and/or " +
							"modify it under the terms of the GNU General Public License " +
							"as published by the Free Software Foundation; either version 2 " +
							"of the License, or (at your option) any later version.\n\n" +
							NAME + " is distributed in the hope that it will be useful, " +
							"but WITHOUT ANY WARRANTY; without even the implied warranty of " +
							"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
							"GNU General Public License for more details.");
		
		Label fullLicenseLabel = createLinkLabel("Full license", Version.FULL_LICENSE_URL);
		fullLicenseLabel.setLocation(leftBound, licenseText.getBounds().y + licenseText.getBounds().height + 10);

		Label privacyLabel = createLinkLabel("Privacy", Version.PRIVACY_URL);
		privacyLabel.setLocation(leftBound + privacyLabel.getBounds().width + 40, fullLicenseLabel.getBounds().y);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.SHEET;
	}

	private Label createLinkLabel(final String text, final String url) {
		final Label link = new Label(shell, SWT.NONE);
		link.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_LINK_FOREGROUND));
		link.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		link.setText(text);
		link.addListener(SWT.MouseUp, event -> BrowserLauncher.openURL(url));
		link.pack();
		return link;
	}
}
