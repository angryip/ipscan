/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import net.azib.ipscan.config.Labels;

import org.junit.Test;

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
		feeder = new RescanFeeder(createMockFeeder(), "123");
		assertEquals("SomeInfo", feeder.getInfo());
		assertEquals("someLabel", feeder.getId());
		assertEquals(Labels.getLabel("feeder.rescan.of") + "someName", feeder.getName());
	}
	
	@Test
	public void testFunctionality() throws Exception {		
		feeder = new RescanFeeder(createMockFeeder(), "127.0.0.15", "127.0.1.35", "127.0.2.2");
		
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
	
	private Feeder createMockFeeder() {
		Feeder feeder = createMock(Feeder.class);
		expect(feeder.getInfo()).andReturn("SomeInfo");
		expect(feeder.getId()).andReturn("someLabel");
		expect(feeder.getName()).andReturn("someName");
		replay(feeder);
		return feeder;
	}

}
