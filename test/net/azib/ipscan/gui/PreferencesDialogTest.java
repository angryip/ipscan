/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.azib.ipscan.gui.PreferencesDialog.PortsTextValidationListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;

/**
 * PreferencesDialogTest
 *
 * @author Anton Keks
 */
public class PreferencesDialogTest {
	
	private PortsTextValidationListener portsTextListener;
	
	private KeyEvent initPortsTextListener() {
		portsTextListener = new PortsTextValidationListener();
		Event ev = new Event();
		ev.widget = mock(Text.class);
		ev.doit = true;
		return new KeyEvent(ev);
	}
	
	@Test
	public void portsTextTraversesOnTab() throws Exception {
		KeyEvent e = initPortsTextListener();
		e.keyCode = SWT.TAB;
		Shell shell = mock(Shell.class);
		when(((Control)e.getSource()).getShell()).thenReturn(shell);
		when(shell.traverse(SWT.TRAVERSE_TAB_NEXT)).thenReturn(true);

		portsTextListener.keyPressed(e);
		assertFalse(e.doit);
	}
	
	@Test
	public void portsTextTraversesOnEnter() throws Exception {
		KeyEvent e = initPortsTextListener();
		e.keyCode = SWT.CR;
		Shell shell = mock(Shell.class);
		when(((Control)e.getSource()).getShell()).thenReturn(shell);
		when(shell.traverse(SWT.TRAVERSE_RETURN)).thenReturn(true);

		portsTextListener.keyPressed(e);
		assertFalse(e.doit);
	}

	@Test
	public void portsTextInsertsNewLineOnCtrlEnter() throws Exception {
		KeyEvent e = initPortsTextListener();
		e.character = SWT.CR;
		e.keyCode = SWT.CR;
		e.stateMask = SWT.MOD1; // is Ctrl on most platforms
		when(((Text)e.widget).getText()).thenReturn("1,");
		when(((Text)e.widget).getCaretPosition()).thenReturn(2);

		portsTextListener.keyPressed(e);
		assertEquals(0, e.stateMask);
		assertTrue(e.doit);
	}

	@Test
	public void portsTextControlCharsBypassed() throws Exception {
		KeyEvent e = initPortsTextListener();
		e.character = 0;	// ISO control char
		portsTextListener.keyPressed(e);
		assertTrue("ISO control chars must be passed through", e.doit);
	}
	
	@Test
	public void testPortsTextValidationListenerLogic() throws Exception {
		PortsTextValidationListener listener = new PortsTextValidationListener();
		assertFalse(listener.validateChar('-', "", 0));
		assertFalse(listener.validateChar('-', "-", 0));
		assertFalse(listener.validateChar('-', ",", 0));
		assertFalse(listener.validateChar('\n', "", 0));

		assertFalse(listener.validateChar(',', ",", 0));
		assertFalse(listener.validateChar(',', "-", 0));

		assertFalse(listener.validateChar(',', "12,3", 3));
		assertTrue(listener.validateChar(',', "1234", 2));
		assertTrue(listener.validateChar('-', "1234", 2));
		
		assertTrue(listener.validateChar(' ', "123,", 4));
		assertTrue(listener.validateChar('\n', "123,", 4));
		assertTrue(listener.validateChar('7', "123,1-3,1-", 10));
		assertTrue(listener.validateChar('3', "1,   ", 4));
	}
}
