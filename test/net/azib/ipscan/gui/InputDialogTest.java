/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import static org.junit.Assert.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Event;
import org.junit.After;
import org.junit.Test;

/**
 * InputDialogTest
 *
 * @author Anton Keks
 */
public class InputDialogTest {
	
	private InputDialog dialog = new InputDialog("title", "msg") {
		@Override
		public void open() {
			// do not open anything in tests
		}
	};
	
	@After
	public void dispose() {
		if (!dialog.text.isDisposed())
			dialog.text.getShell().dispose();		
	}

	@Test
	public void titleAndMessageDisplayed() throws Exception {
		assertEquals("msg", dialog.messageLabel.getText());
		assertEquals("title", dialog.messageLabel.getShell().getText());
	}
	
	@Test
	public void defaultText() throws Exception {
		dialog.open("hello");
		assertEquals("hello", dialog.text.getSelectionText());
	}
	
	@Test
	public void nullText() throws Exception {
		dialog.open(null);
		assertEquals("", dialog.text.getText());
		assertTrue(((FormData)dialog.text.getLayoutData()).width > 200);
	}

	@Test
	public void openReturnsEnteredText() throws Exception {
		dialog = new InputDialog("title", "msg") {
			@Override
			public void open() {
				okButton.notifyListeners(SWT.Selection, new Event());
			}
		};
		assertEquals("foo", dialog.open("foo"));
	}
}
