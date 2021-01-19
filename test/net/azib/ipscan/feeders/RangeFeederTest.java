package net.azib.ipscan.feeders;

import org.junit.Test;

import static net.azib.ipscan.feeders.FeederTestUtils.assertFeederException;
import static org.junit.Assert.*;

public class RangeFeederTest {
	@Test
	public void forward() throws FeederException {
		RangeFeeder feeder = new RangeFeeder("10.11.12.13", "10.11.12.15");
		assertTrue(feeder.hasNext());
		assertEquals("10.11.12.13", feeder.next().getAddress().getHostAddress());
		assertTrue(feeder.hasNext());
		assertEquals("10.11.12.14", feeder.next().getAddress().getHostAddress());
		assertTrue(feeder.hasNext());
		assertEquals("10.11.12.15", feeder.next().getAddress().getHostAddress());
		assertFalse(feeder.hasNext());
	}
	
	@Test
	public void reverse() {
		RangeFeeder feeder = new RangeFeeder("10.11.12.13", "10.11.12.11");
		assertTrue(feeder.isReverse);
		assertEquals("10.11.12.13", feeder.next().getAddress().getHostAddress());
		assertEquals("10.11.12.12", feeder.next().getAddress().getHostAddress());
		assertEquals("10.11.12.11", feeder.next().getAddress().getHostAddress());
		assertFalse(feeder.hasNext());
	}

	@Test
	public void malformedIP() {
		try {
			new RangeFeeder("10.11.12.blah.", "10.11.12.10");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
		try {
			new RangeFeeder("10.11.12.1", "ziga,");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
	}
	
	@Test
	public void extremeValues() {
		RangeFeeder rangeFeeder = null; 
		
		rangeFeeder = new RangeFeeder("0.0.0.0", "0.0.0.0");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("0.0.0.0", rangeFeeder.next().getAddress().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
		
		rangeFeeder = new RangeFeeder("255.255.255.255", "255.255.255.255");
		assertTrue(rangeFeeder.hasNext());
		assertEquals("255.255.255.255", rangeFeeder.next().getAddress().getHostAddress());
		assertFalse(rangeFeeder.hasNext());
	}
		
	@Test
	public void getPercentageComplete() {
		RangeFeeder rangeFeeder = new RangeFeeder("100.11.12.13", "100.11.12.15");
		assertEquals(0, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(33, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(67, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.percentageComplete());
		
		rangeFeeder = new RangeFeeder("255.255.255.255", "255.255.255.255");
		assertEquals(0, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.percentageComplete());
	}

	@Test
	public void getPercentageCompleteIPv6() {
		RangeFeeder rangeFeeder = new RangeFeeder("::1", "::3");
		assertEquals(0, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(33, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(67, rangeFeeder.percentageComplete());
		rangeFeeder.next();
		assertEquals(100, rangeFeeder.percentageComplete());
	}

	@Test
	public void getInfo() {
		RangeFeeder rangeFeeder = new RangeFeeder("100.11.12.13", "100.11.12.13");
		assertEquals("100.11.12.13 - 100.11.12.13", rangeFeeder.getInfo());
		rangeFeeder = new RangeFeeder("0.0.0.0", "255.255.255.255");
		assertEquals("0.0.0.0 - 255.255.255.255", rangeFeeder.getInfo());
	}
}
