/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.actions.BrowserLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import javax.inject.Inject;

import static net.azib.ipscan.config.Version.*;

/**
 * About Dialog
 *
 * @author Anton Keks
 */
public class AboutDialog extends AbstractModalDialog {

	@Inject
	public AboutDialog() {
	}

	@Override
	protected void populateShell() {
		shell.setText(Labels.getLabel("title.about"));
		shell.setSize(new Point(400, 393));

		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		if (shell.getImage() != null) {
			iconLabel.setImage(shell.getImage());
		}		
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 20;

		String aboutText = Labels.getLabel("text.about");
		aboutText = aboutText.replace("%NAME", NAME);
		aboutText = aboutText.replace("%VERSION", getVersion());
		aboutText = aboutText.replace("%DATE", getBuildDate());
		aboutText = aboutText.replace("%COPYLEFT", COPYLEFT);
		Label aboutLabel = new Label(shell, SWT.NONE);
		aboutLabel.setText(aboutText);
		aboutLabel.setLocation(leftBound, 10);
		aboutLabel.pack();
		
		Label websiteLabel = createLinkLabel(WEBSITE, WEBSITE);
		websiteLabel.setLocation(leftBound, 10 + aboutLabel.getBounds().height);

		String systemText = Labels.getLabel("text.about.system");
		systemText = systemText.replace("%JAVA", System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));
		systemText = systemText.replace("%OS", System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
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
		link.setForeground(new Color(null, 0, 0, 0xCC));
		link.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		link.setText(text);
		link.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				BrowserLauncher.openURL(url);
				link.setForeground(new Color(null, 0x88, 0, 0xAA));
			}
		});
		link.pack();
		return link;
	}

}
