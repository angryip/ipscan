package net.azib.ipscan.feeders;

import org.junit.Test;

import static net.azib.ipscan.feeders.FeederTestUtils.assertFeederException;
import static org.junit.Assert.*;

/**
 * Test of RandomFeeder
 *
 * @author Anton Keks
 */
public class RandomFeederTest {

	@Test
	public void testHappyPath() throws FeederException {
		var randomFeeder = new RandomFeeder("255.255.255.255", "255...0", 2);
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getAddress().getHostAddress().startsWith("255.255.255"));
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getAddress().getHostAddress().startsWith("255.255.255"));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testInvalidCount() {
		try {
			new RandomFeeder("1.1.1.1", "1.1.1.1", 0);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("random.invalidCount", e);
		}
	}
	
	@Test
	public void testMalformedIP() {
		try {
			new RandomFeeder("{123}", "10.11.12.10", 1);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
	}
	
	@Test
	public void testInvalidNetmask() {
		try {
			new RandomFeeder("1.1.1.1", "<invalid>", 1);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("invalidNetmask", e);
		}
	}
	
	@Test
	public void testFullMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder("1.2.3.45", "255.255.255.255", 1);
		assertTrue(randomFeeder.hasNext());
		assertEquals("1.2.3.45", randomFeeder.next().getAddress().getHostAddress());
		assertFalse(randomFeeder.hasNext());
	}
		
	@Test
	public void testEmptyMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder("1.2.3.45", "0.0.0.0", 1);
		assertTrue(randomFeeder.hasNext());
		assertFalse("1.2.3.45".equals(randomFeeder.next().getAddress().getHostAddress()));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testMaskStartEnd() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder("1.2.3.45", "255.0.0.255", 1);
		assertTrue(randomFeeder.hasNext());
		var address = randomFeeder.next().getAddress().getHostAddress();
		assertTrue(address.startsWith("1."));
		assertTrue(address.endsWith(".45"));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testDifferent() {
		RandomFeeder randomFeeder = null;
		String address = null;
		
		randomFeeder = new RandomFeeder("1.2.3.45", "255.0.255.0", 2);
		assertTrue(randomFeeder.hasNext());
		address = randomFeeder.next().getAddress().getHostAddress();
		assertFalse("1.2.3.45".equals(address));
		assertTrue(randomFeeder.hasNext());
		assertFalse(address.equals(randomFeeder.next().getAddress().getHostAddress()));
		assertFalse(randomFeeder.hasNext());

		randomFeeder = new RandomFeeder("1.2.3.45", "255.0.127.0", 1);
		assertTrue(randomFeeder.hasNext());
		assertFalse(address.equals(randomFeeder.next().getAddress().getHostAddress()));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testGetPercentageComplete() throws Exception {
		var randomFeeder = new RandomFeeder("100.11.12.13", "100.11.12.15", 3);
		assertEquals(0, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(33, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(67, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.percentageComplete());
		
		randomFeeder = new RandomFeeder("255.255.255.255", "255.255.255.255", 1);
		assertEquals(0, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.percentageComplete());
	}	

	@Test
	public void testGetInfo() {
		var randomFeeder = new RandomFeeder("100.11.12.13", "100.11.12.15", 3);
		assertEquals("3: 100.11.12.13 / 100.11.12.15", randomFeeder.getInfo());
		randomFeeder = new RandomFeeder("0.0.0.0", "255.255.255.255", 129876);
		assertEquals("129876: 0.0.0.0 / 255.255.255.255", randomFeeder.getInfo());
	}
}
