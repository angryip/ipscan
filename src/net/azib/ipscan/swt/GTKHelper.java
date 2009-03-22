/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.swt;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Button;

/**
 * Helper class used internally by SWTHelper to extend functionality of SWT in some ways
 *
 * @author Anton Keks
 */
class GTKHelper {
	public static final int GTK_ICON_SIZE_BUTTON = OS.GTK_ICON_SIZE_LARGE_TOOLBAR+1;

	/**
	 * Sets the stock icon to the specified widget
	 * @param button
	 * @param iconConstant SWT.OK, SWT.CANCEL, etc
	 */
	public static void setStockIconFor(Button button, int iconConstant) {
		String name;
		switch (iconConstant) {
			case SWT.OK:
				name = "gtk-ok"; break;
			case SWT.CANCEL:
				name = "gtk-cancel"; break;
			case SWT.ABORT:
				name = "gtk-close"; break;
			case SWT.ARROW_RIGHT:	// gtk-go-forward doesn't work...
			case SWT.YES:
				name = "gtk-yes"; break;
			case SWT.NO:
				name = "gtk-no"; break;
			case SWTHelper.FIND:
				name = "gtk-find"; break;
			default:
				return;
		}
		
		try {
			// use reflection to avoid compile-time dependency on other platforms
			Class<?> displayExt = Class.forName("net.azib.ipscan.platform.swt.DisplayExt");
			Method createImage = displayExt.getDeclaredMethod("createImage", String.class, int.class);
			Image icon = (Image) createImage.invoke(null, name, GTK_ICON_SIZE_BUTTON);
			if (icon != null) {
				button.setImage(icon);
			}
		}
		catch (Exception e) {
			Logger.getLogger(GTKHelper.class.getName()).warning(e.toString());
		}
	}
}
