/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;


/**
 * OptionsDialogTest
 *
 * @author Anton Keks Keks
 */
public class OptionsDialogTest {
	
	@Test
	public void testPortsTextValidationListenerKeyPressed() throws Exception {
		KeyListener listener = new OptionsDialog.PortsTextValidationListener();
		Event ev = new Event();
		ev.widget = new Shell();
		ev.doit = true;
		KeyEvent e = new KeyEvent(ev);
		
		e.character = 0;	// ISO control char
		listener.keyPressed(e);
		assertTrue("ISO control chars must be passed through", e.doit);
	}
	
	@Test
	public void testPortsTextValidationListenerLogic() throws Exception {
		OptionsDialog.PortsTextValidationListener listener = new OptionsDialog.PortsTextValidationListener();
		assertFalse(listener.validateChar('-', "", 0));
		assertFalse(listener.validateChar('-', "-", 0));
		assertFalse(listener.validateChar('-', ",", 0));

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
