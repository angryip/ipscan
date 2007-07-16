/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.junit.Test;

/**
 * RescanFeederTest
 *
 * @author Anton Keks Keks
 */
public class RescanFeederTest {
	
	private Feeder feeder = new RescanFeeder(createMockFeeder());
	
	@Test(expected=IllegalArgumentException.class)
	public void testEmpty() throws Exception {
		feeder.initialize();
	}
	
	@Test
	public void testDelegatedMethods() {
		assertEquals("SomeInfo", feeder.getInfo());
		assertEquals("someLabel", feeder.getLabel());
	}
	
	@Test
	public void testFunctionality() throws Exception {		
		assertEquals(3, feeder.initialize("127.0.0.15", "127.0.1.35", "127.0.2.2"));
		
		assertTrue(feeder.hasNext());
		assertEquals(0, feeder.percentageComplete());
		assertEquals("127.0.0.15", feeder.next().getHostAddress());

		assertTrue(feeder.hasNext());
		assertEquals(33, feeder.percentageComplete());
		assertEquals("127.0.1.35", feeder.next().getHostAddress());
		
		assertTrue(feeder.hasNext());
		assertEquals(66, feeder.percentageComplete());
		assertEquals("127.0.2.2", feeder.next().getHostAddress());

		assertFalse(feeder.hasNext());
		assertEquals(100, feeder.percentageComplete());
	}
	
	private Feeder createMockFeeder() {
		Feeder feeder = createMock(Feeder.class);
		expect(feeder.getInfo()).andReturn("SomeInfo");
		expect(feeder.getLabel()).andReturn("someLabel");
		replay(feeder);
		return feeder;
	}

}
