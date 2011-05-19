/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;

import org.junit.Test;

/**
 * StatisticsDialogTest
 *
 * @author Anton Keks
 */
public class StatisticsDialogTest {
	
	@Test
	public void timeToText() throws Exception {
		Labels.initialize(new Locale("en")); 
		assertEquals("0\u00A0sec", StatisticsDialog.timeToText(0));
		assertEquals("0.5\u00A0sec", StatisticsDialog.timeToText(499));
		assertEquals("0.3\u00A0sec", StatisticsDialog.timeToText(265));
		assertEquals("1\u00A0sec", StatisticsDialog.timeToText(1001));
		assertEquals("1.5\u00A0min", StatisticsDialog.timeToText(90025));
		assertEquals("10\u00A0min", StatisticsDialog.timeToText(600000));
		assertEquals("1\u00A0h", StatisticsDialog.timeToText(3600000));
		assertEquals("2.5\u00A0h", StatisticsDialog.timeToText(9036000));
	}
	
	@Test
	public void dialogContent() throws Exception {
		ScanningResultList results = mock(ScanningResultList.class);
		ScanInfo scanInfo = new ScanInfo() {
			{
				this.startTime = System.currentTimeMillis();
				this.endTime = this.startTime + 10000;
				this.numScanned = 20;
				this.numAlive = 10;
				this.numWithPorts = 5;
			}
		};
		
		when(results.getScanInfo()).thenReturn(scanInfo);
		when(results.getFeederName()).thenReturn("SomeFeeder");
		when(results.getFeederInfo()).thenReturn("SomeInfoHere");

		String text = new StatisticsDialog(results).prepareText();
		
		assertNotNull(text);
		assertTrue(text.contains(Labels.getLabel("text.scan.time.total") + "10"));
		assertTrue(text.contains(Labels.getLabel("text.scan.time.average") + "0.5"));
		assertTrue(text.contains("SomeFeeder"));
		assertTrue(text.contains("SomeInfoHere"));
		assertTrue(text.contains(Labels.getLabel("text.scan.hosts.total") + "20"));
		assertTrue(text.contains(Labels.getLabel("text.scan.hosts.alive") + "10"));
		assertTrue(text.contains(Labels.getLabel("text.scan.hosts.ports") + "5"));
	}
}
