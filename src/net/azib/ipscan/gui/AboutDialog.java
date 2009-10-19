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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * About Dialog
 *
 * @author Anton Keks
 */
public class AboutDialog extends AbstractModalDialog {

	@Override
	protected void populateShell() {
		shell.setText(Labels.getLabel("title.about"));
		shell.setSize(new Point(400, 373));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLocation(10, 10);
		if (shell.getImage() != null) {
			iconLabel.setImage(shell.getImage());
		}		
		iconLabel.pack();
		int leftBound = iconLabel.getBounds().width + 20;

		String aboutText = Labels.getLabel("text.about");
		aboutText = aboutText.replaceAll("%NAME", Version.NAME);
		aboutText = aboutText.replaceAll("%VERSION", Version.getVersion());
		aboutText = aboutText.replaceAll("%DATE", Version.getBuildDate());
		aboutText = aboutText.replaceAll("%COPYLEFT", Version.COPYLEFT);
		Label aboutLabel = new Label(shell, SWT.NONE);
		aboutLabel.setText(aboutText);
		aboutLabel.setLocation(leftBound, 10);
		aboutLabel.pack();
		
		final Label websiteLabel = new Label(shell, SWT.NONE);
		websiteLabel.setForeground(new Color(null, 0, 0, 0xCC));
		websiteLabel.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		websiteLabel.setText(Version.WEBSITE);
		websiteLabel.setLocation(leftBound, 10 + aboutLabel.getBounds().height);
		websiteLabel.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event event) {
				BrowserLauncher.openURL(Version.WEBSITE);
				websiteLabel.setForeground(new Color(null, 0x88, 0, 0xAA));
			}
		});
		websiteLabel.pack();

		String systemText = Labels.getLabel("text.about.system");
		systemText = systemText.replaceAll("%JAVA", System.getProperty("java.vm.vendor") + " " + System.getProperty("java.runtime.version"));
		systemText = systemText.replaceAll("%OS", System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
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
							Version.NAME + " is free software; you can redistribute it and/or " +
							"modify it under the terms of the GNU General Public License " +
							"as published by the Free Software Foundation; either version 2 " +
							"of the License, or (at your option) any later version.\n\n" +
							Version.NAME + " is distributed in the hope that it will be useful, " +
							"but WITHOUT ANY WARRANTY; without even the implied warranty of " +
							"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
							"GNU General Public License for more details.\n\n" +
							"You should have received a copy of the GNU General Public License " +
							"along with this program; if not, write to the Free Software " +
							"Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA " +
							"02110-1301, USA, or visit http://www.fsf.org/");
	}
	
}
