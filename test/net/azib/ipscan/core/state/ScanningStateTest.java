/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.state;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * ScanningStateTest
 *
 * @author Anton Keks Keks
 */
public class ScanningStateTest {
	
	@Test
	public void testNext() throws Exception {
		assertEquals(ScanningState.STARTING, ScanningState.IDLE.next());
		assertEquals(ScanningState.SCANNING, ScanningState.STARTING.next());
		assertEquals(ScanningState.SCANNING, ScanningState.RESTARTING.next());
		assertEquals(ScanningState.STOPPING, ScanningState.SCANNING.next());
		assertEquals(ScanningState.KILLING, ScanningState.STOPPING.next());
		assertNull(ScanningState.KILLING.next());
	}
}
