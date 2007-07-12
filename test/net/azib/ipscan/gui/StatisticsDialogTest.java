/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import net.azib.ipscan.config.Labels;

import org.junit.Test;

/**
 * StatisticsDialogTest
 *
 * @author Anton Keks
 */
public class StatisticsDialogTest {
	
	@Test
	public void testTimeToText() throws Exception {
		Labels.initialize(new Locale("en")); 
		assertEquals("0\u00A0sec", StatisticsDialog.timeToText(0));
		assertEquals("0.5\u00A0sec", StatisticsDialog.timeToText(499));
		assertEquals("0.3\u00A0sec", StatisticsDialog.timeToText(265));
		assertEquals("1\u00A0sec", StatisticsDialog.timeToText(1001));
		assertEquals("1.5\u00A0min", StatisticsDialog.timeToText(90025));
		assertEquals("10\u00A0min", StatisticsDialog.timeToText(600000));
	}
}
