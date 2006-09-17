/**
 * 
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Version;
import net.azib.ipscan.gui.actions.HelpActions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * About Window
 *
 * @author anton
 */
public class AboutWindow extends AbstractModalDialog {

	public AboutWindow() {
		createShell();		
	}
	
	/**
	 * This method initializes shell
	 */
	private void createShell() {
		Display currentDisplay = Display.getCurrent();
		Shell parent = currentDisplay != null ? currentDisplay.getActiveShell() : null;
		shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);

		shell.setText(Labels.getInstance().getString("title.about"));
		shell.setSize(new Point(400, 363));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setBounds(10, 10, 0, 0);
		
		if (parent != null) {
			iconLabel.setImage(parent.getImage());
			shell.setImage(parent.getImage());
		}		
		iconLabel.pack();

		// TODO: make clicking on links work
		Link textLabel = new Link(shell, SWT.NONE);
		String text = Labels.getInstance().getString("text.about");
		text = text.replaceAll("%NAME", Version.NAME);
		text = text.replaceAll("%VERSION", Version.VERSION);
		text = text.replaceAll("%COPYLEFT", Version.COPYLEFT);
		text = text.replaceAll("%WEBSITE", Version.WEBSITE);
		text = text.replaceAll("%MAILTO", Version.MAILTO);
		textLabel.setText(text);
		textLabel.setBounds(60, 10, 0, 0);
		textLabel.addListener(SWT.Selection, new HelpActions.Website());
		textLabel.pack();
		
		Text licenseText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP);
		licenseText.setBounds(60, 140, 320, 160);
		licenseText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		// TODO: load full GPL text from file
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
		
		Button button = new Button(shell, SWT.NONE);
		button.setText(Labels.getInstance().getString("button.close"));
		button.setBounds(170, 305, 80, 25);
		button.setFocus();
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.close();
				shell.dispose();
			}
		});
		
		shell.setDefaultButton(button);
	}
	
}
