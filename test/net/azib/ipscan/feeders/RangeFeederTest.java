package net.azib.ipscan.feeders;

import static net.azib.ipscan.feeders.FeederTestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of RangeFeeder 
 * 
 * @author Anton Keks
 */
public class RangeFeederTest {

	@Test
	public void testHappyPath() throws FeederException {
		RangeFeeder rangeFeeder = new RangeFeeder();
		assertEquals(2, rangeFeeder.initialize(new String[] {"10.11.12.13", "10.11.12.15"}));
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.13", rangeFeeder.next().getHostAddress());
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.14", rangeFeeder.next().getHostAddress());
		assertTrue(rangeFeeder.hasNext());
		assertEquals("10.11.12.15", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
	}
	
	@Test
	public void testInvalidRange() {
		try {
			new RangeFeeder().initialize("10.11.12.13", "10.11.12.10");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("range.greaterThan", e);
		}
	}

	@Test
	public void testMalformedIP() {
		try {
			new RangeFeeder().initialize("10.11.12.abc", "10.11.12.10");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
		try {
			new RangeFeeder().initialize("10.11.12.1", "ziga");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
	}
	
	@Test
	public void testExtremeValues() {
		RangeFeeder rangeFeeder = null; 
		
		rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("0.0.0.0", "0.0.0.0");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("0.0.0.0", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
		
		rangeFeeder.initialize("255.255.255.255", "255.255.255.255");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("255.255.255.255", rangeFeeder.next().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
	}
		
	@Test
	public void testGetPercentageComplete() throws Exception {
		RangeFeeder rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("100.11.12.13", "100.11.12.15");
		assertEquals(0, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(33, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(67, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.percentageComplete());
		
		rangeFeeder.initialize("255.255.255.255", "255.255.255.255");
		assertEquals(0, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.percentageComplete());
	}
	
	@Test
	public void testGetInfo() {
		RangeFeeder rangeFeeder = new RangeFeeder();
		rangeFeeder.initialize("100.11.12.13", "100.11.12.13");
		assertEquals("100.11.12.13 - 100.11.12.13", rangeFeeder.getInfo());
		rangeFeeder.initialize("0.0.0.0", "255.255.255.255");
		assertEquals("0.0.0.0 - 255.255.255.255", rangeFeeder.getInfo());
	}
}
