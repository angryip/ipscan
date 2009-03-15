/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.platform.linux;

import net.azib.ipscan.platform.SWTHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.internal.Converter;
import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.widgets.Button;

/**
 * Helper class used internally by SWTHelper to extend functionality of SWT in some ways
 *
 * @author Anton Keks
 */
public class GTKHelper {
	
	/**
	 * Sets the stock icon to the specified widget
	 * @param button
	 * @param iconConstant SWT.OK, SWT.CANCEL, etc
	 */
	@SuppressWarnings("static-access")
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
		
		// copy-paste from Display.setImage() follows, with replaced GTK_ICON_SIZE_MENU
		
		int /*long*/ style = OS.gtk_widget_get_default_style ();
		byte[] buffer = Converter.wcsToMbcs (null, name, true);
		int /*long*/ pixbuf = OS.gtk_icon_set_render_icon (
			OS.gtk_icon_factory_lookup_default (buffer), style,
			OS.GTK_TEXT_DIR_NONE, OS.GTK_STATE_NORMAL, OS.GTK_ICON_SIZE_LARGE_TOOLBAR+1 /* OS.GTK_ICON_SIZE_BUTTON */, 0, 0);
		if (pixbuf == 0) return;
		int width = OS.gdk_pixbuf_get_width (pixbuf);
		int height = OS.gdk_pixbuf_get_height (pixbuf);
		int stride = OS.gdk_pixbuf_get_rowstride (pixbuf);
		boolean hasAlpha = OS.gdk_pixbuf_get_has_alpha (pixbuf);
		int /*long*/ pixels = OS.gdk_pixbuf_get_pixels (pixbuf);
		byte [] data = new byte [stride * height];
		OS.memmove (data, pixels, data.length);
		OS.g_object_unref (pixbuf);
		ImageData imageData = null;
		if (hasAlpha) {
			PaletteData palette = new PaletteData (0xFF000000, 0xFF0000, 0xFF00);
			imageData = new ImageData (width, height, 32, palette);
			byte [] alpha = new byte [stride * height];
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					alpha [y*width+x] = data [y*stride+x*4+3];
					data [y*stride+x*4+3] = 0;
				}
			}
			imageData.setAlphas (0, 0, width * height, alpha, 0);
		} else {
			PaletteData palette = new PaletteData (0xFF0000, 0xFF00, 0xFF);
			imageData = new ImageData (width, height, 24, palette);
		}
		imageData.data = data;
		imageData.bytesPerLine = stride;
		Image icon = new Image (button.getDisplay(), imageData);
		
		button.setImage(icon);
	}
}
