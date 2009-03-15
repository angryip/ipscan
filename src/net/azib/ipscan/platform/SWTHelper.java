/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.platform;

import net.azib.ipscan.config.Platform;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

/**
 * SWTHelper
 *
 * @author Anton Keks
 */
public class SWTHelper {
	public static final int FIND = 987;
	public static final int CLOSE = SWT.ABORT;

	/**
	 * Sets the stock icon to the specified widget
	 * @param button
	 * @param iconConstant SWT.OK, SWT.CANCEL, etc
	 */
	public static void setStockIconFor(Button button, int iconConstant) {
		if (button == null || button.getImage() != null)
			return;
		
		if (Platform.LINUX) {
			net.azib.ipscan.platform.linux.GTKHelper.setStockIconFor(button, iconConstant);
		}
	}
}
