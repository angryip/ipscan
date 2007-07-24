/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

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
}
