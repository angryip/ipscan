/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * RescanFeederTest
 *
 * @author Anton Keks
 */
public class RescanFeederTest {
	
	private Feeder feeder;
	
	@Test(expected=IllegalArgumentException.class)
	public void testEmpty() throws Exception {
		new RescanFeeder(null);
	}
	
	@Test
	public void testDelegatedMethods() {
		feeder = new RescanFeeder(mockFeeder(), "123");
		assertEquals("SomeInfo", feeder.getInfo());
		assertEquals("someLabel", feeder.getId());
		assertEquals(Labels.getLabel("feeder.rescan.of") + "someName", feeder.getName());
	}
	
	@Test
	public void addresses() {
		feeder = new RescanFeeder(mockFeeder(), "127.0.0.15", "127.0.1.35", "127.0.2.2");
		
		assertTrue(feeder.hasNext());
		assertEquals(0, feeder.percentageComplete());
		assertEquals("127.0.0.15", feeder.next().getAddress().getHostAddress());

		assertTrue(feeder.hasNext());
		assertEquals(33, feeder.percentageComplete());
		assertEquals("127.0.1.35", feeder.next().getAddress().getHostAddress());
		
		assertTrue(feeder.hasNext());
		assertEquals(66, feeder.percentageComplete());
		assertEquals("127.0.2.2", feeder.next().getAddress().getHostAddress());

		assertFalse(feeder.hasNext());
		assertEquals(100, feeder.percentageComplete());
	}
	
	private Feeder mockFeeder() {
		Feeder feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn("SomeInfo");
		when(feeder.getId()).thenReturn("someLabel");
		when(feeder.getName()).thenReturn("someName");
		when(feeder.subject(any())).thenAnswer(i -> new ScanningSubject(i.getArgument(0)));
		return feeder;
	}

}
