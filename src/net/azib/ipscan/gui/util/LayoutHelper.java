/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.DPIUtil;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A helper class to create FormLayout and FormData object more conveniently.
 *
 * @author Anton Keks
 */
public class LayoutHelper {

	public static FormLayout formLayout(int marginWidth, int marginHeight, int spacing) {
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = marginWidth;
		formLayout.marginHeight = marginHeight;
		formLayout.spacing = spacing;
		return formLayout;
	}
	
	public static FormData formData(int width, int height, FormAttachment left, FormAttachment right, FormAttachment top, FormAttachment bottom) {
		FormData formData = new FormData(width, height);
		formData.left = left;
		formData.right = right;
		formData.top = top;
		formData.bottom = bottom;
		return formData;
	}
	
	public static FormData formData(FormAttachment left, FormAttachment right, FormAttachment top, FormAttachment bottom) {
		return formData(SWT.DEFAULT, SWT.DEFAULT, left, right, top, bottom);
	}

	public static Font iconFont(Shell shell) {
		FontData fontData = shell.getFont().getFontData()[0];
		fontData.setHeight(fontData.getHeight() * 4/3);
		return new Font(shell.getDisplay(), fontData);
	}

	public static Image buttonImage(final String baseName) {
		final Display display = Display.getCurrent();
		return new Image(display, new ImageDataProvider() {
			@Override public ImageData getImageData(int zoom) {
				String suffix = zoom == 200 ? "@2x.png" : ".png";
				ImageData imageData = new ImageData(getClass().getResourceAsStream("/images/buttons/" + baseName + suffix));
				if (zoom != 100 & zoom != 200)
					imageData = DPIUtil.autoScaleUp(display, imageData);
				return imageData;
			}
		});
	}
}
