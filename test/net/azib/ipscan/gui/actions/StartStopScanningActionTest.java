/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.actions;

import static org.junit.Assert.assertNotNull;
import net.azib.ipscan.core.state.ScanningState;

import org.eclipse.swt.widgets.Display;
import org.junit.Test;


/**
 * StartStopScanningActionTest
 *
 * @author Anton Keks
 */
public class StartStopScanningActionTest {
	
	@Test
	public void testAllImagesAreDefined() throws Exception {
		StartStopScanningAction action = new StartStopScanningAction(Display.getDefault());
		for (ScanningState state : ScanningState.values()) {
			assertNotNull(action.buttonImages[state.ordinal()]);
			assertNotNull(action.buttonTexts[state.ordinal()]);
		}
	}
}
