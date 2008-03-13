package net.azib.ipscan.feeders;

import static net.azib.ipscan.feeders.FeederTestUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test of RandomFeeder
 *
 * @author Anton Keks
 */
public class RandomFeederTest {

	@Test
	public void testHappyPath() throws FeederException {
		RandomFeeder randomFeeder = new RandomFeeder();
		assertEquals(3, randomFeeder.initialize(new String[] {"255.255.255.255", "255...0", "2"}));
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getHostAddress().startsWith("255.255.255"));
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getHostAddress().startsWith("255.255.255"));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testInvalidCount() {
		try {
			new RandomFeeder().initialize(new String[] {"1.1.1.1", "2.2.2.2", "abc"});
			fail();
		}
		catch (FeederException e) {
			assertFeederException("random.invalidCount", e);
		}
		try {
			new RandomFeeder().initialize("1.1.1.1", "1.1.1.1", 0);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("random.invalidCount", e);
		}
	}
	
	@Test
	public void testMalformedIP() {
		try {
			new RandomFeeder().initialize("{123}", "10.11.12.10", 1);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("malformedIP", e);
		}
	}
	
	@Test
	public void testInvalidNetmask() {
		try {
			new RandomFeeder().initialize("1.1.1.1", "<invalid>", 1);
			fail();
		}
		catch (FeederException e) {
			assertFeederException("invalidNetmask", e);
		}
	}
	
	@Test
	public void testFullMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "255.255.255.255", 1);
		assertTrue(randomFeeder.hasNext());
		assertEquals("1.2.3.45", randomFeeder.next().getHostAddress());
		assertFalse(randomFeeder.hasNext());
	}
		
	@Test
	public void testEmptyMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "0.0.0.0", 1);
		assertTrue(randomFeeder.hasNext());
		assertFalse("1.2.3.45".equals(randomFeeder.next().getHostAddress()));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testMaskStartEnd() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "255.0.0.255", 1);
		assertTrue(randomFeeder.hasNext());
		String address = randomFeeder.next().getHostAddress();
		assertTrue(address.startsWith("1."));
		assertTrue(address.endsWith(".45"));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testDifferent() {
		RandomFeeder randomFeeder = null;
		String address = null;
		
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "255.0.255.0", 2);
		assertTrue(randomFeeder.hasNext());
		address = randomFeeder.next().getHostAddress();
		assertFalse("1.2.3.45".equals(address));
		assertTrue(randomFeeder.hasNext());
		assertFalse(address.equals(randomFeeder.next().getHostAddress()));
		assertFalse(randomFeeder.hasNext());

		randomFeeder.initialize("1.2.3.45", "255.0.127.0", 1);
		assertTrue(randomFeeder.hasNext());
		assertFalse(address.equals(randomFeeder.next().getHostAddress()));
		assertFalse(randomFeeder.hasNext());
	}
	
	@Test
	public void testGetPercentageComplete() throws Exception {
		RandomFeeder randomFeeder = new RandomFeeder();
		randomFeeder.initialize("100.11.12.13", "100.11.12.15", 3);
		assertEquals(0, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(33, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(67, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.percentageComplete());
		
		randomFeeder.initialize("255.255.255.255", "255.255.255.255", 1);
		assertEquals(0, randomFeeder.percentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.percentageComplete());
	}	

	@Test
	public void testGetInfo() {
		RandomFeeder randomFeeder = new RandomFeeder();
		randomFeeder.initialize("100.11.12.13", "100.11.12.15", 3);
		assertEquals("3: 100.11.12.13 / 100.11.12.15", randomFeeder.getInfo());
		randomFeeder.initialize("0.0.0.0", "255.255.255.255", 129876);
		assertEquals("129876: 0.0.0.0 / 255.255.255.255", randomFeeder.getInfo());
	}
}
