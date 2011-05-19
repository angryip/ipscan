/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import net.azib.ipscan.gui.util.LayoutHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * StatisticsDialog - shows statistical information about the last scan
 *
 * @author Anton Keks
 */
public class InfoDialog extends AbstractModalDialog {
	
	String title;
	String title2;
	String message;
	
	public InfoDialog(String title, String title2) {
		this.title = title;
		this.title2 = title2;
	}

	@Override
	protected void populateShell() {
		shell.setText(title);
		shell.setLayout(LayoutHelper.formLayout(10, 10, 15));
		
		Label iconLabel = new Label(shell, SWT.ICON);
		iconLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(0), null, new FormAttachment(0), null));
		iconLabel.setImage(shell.getImage());
		
		Label titleLabel = new Label(shell, SWT.NONE);
		FontData sysFontData = shell.getDisplay().getSystemFont().getFontData()[0];
		titleLabel.setLayoutData(LayoutHelper.formData(new FormAttachment(iconLabel), null, new FormAttachment(0), null));
		titleLabel.setFont(new Font(null, sysFontData.getName(), sysFontData.getHeight()+3, sysFontData.getStyle() | SWT.BOLD));
		titleLabel.setText(title2);
			
		Text statsText = new Text(shell, SWT.MULTI | SWT.READ_ONLY);
		statsText.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		statsText.setLayoutData(LayoutHelper.formData(new FormAttachment(iconLabel), null, new FormAttachment(titleLabel), null));
		statsText.setText(message);
		statsText.pack();
		
		Button button = createCloseButton();
		Point buttonSize = button.getSize();
		button.setLayoutData(LayoutHelper.formData(buttonSize.x, buttonSize.y, null, new FormAttachment(statsText, 30, SWT.RIGHT), new FormAttachment(statsText), null));
		
		shell.pack();
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
