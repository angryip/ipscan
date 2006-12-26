package net.azib.ipscan.feeders;

import junit.framework.TestCase;

/**
 * Test of RandomFeeder
 *
 * @author anton
 */
public class RandomFeederTest extends TestCase {

	public void testHappyPath() throws FeederException {
		RandomFeeder randomFeeder = new RandomFeeder();
		assertEquals(3, randomFeeder.initialize(new String[] {"255.255.255.255", "255...0", "2"}));
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getHostAddress().startsWith("255.255.255"));
		assertTrue(randomFeeder.hasNext());
		assertTrue(randomFeeder.next().getHostAddress().startsWith("255.255.255"));
		assertFalse(randomFeeder.hasNext());
	}
	
	public void testInvalidCount() {
		try {
			new RandomFeeder().initialize(new String[] {"1.1.1.1", "2.2.2.2", "abc"});
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("random.invalidCount", e);
		}
		try {
			new RandomFeeder().initialize("1.1.1.1", "1.1.1.1", 0);
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("random.invalidCount", e);
		}
	}
	
	public void testMalformedIP() {
		try {
			new RandomFeeder().initialize("abc", "10.11.12.10", 1);
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("malformedIP", e);
		}
	}
	
	public void testInvalidNetmask() {
		try {
			new RandomFeeder().initialize("1.1.1.1", "invalid", 1);
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("invalidNetmask", e);
		}
	}
	
	public void testFullMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "255.255.255.255", 1);
		assertTrue(randomFeeder.hasNext());
		assertEquals("1.2.3.45", randomFeeder.next().getHostAddress());
		assertFalse(randomFeeder.hasNext());
	}
		
	public void testEmptyMask() {
		RandomFeeder randomFeeder = null; 
		randomFeeder = new RandomFeeder();
		randomFeeder.initialize("1.2.3.45", "0.0.0.0", 1);
		assertTrue(randomFeeder.hasNext());
		assertFalse("1.2.3.45".equals(randomFeeder.next().getHostAddress()));
		assertFalse(randomFeeder.hasNext());
	}
	
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
	
	public void testGetPercentageComplete() throws Exception {
		RandomFeeder randomFeeder = new RandomFeeder();
		randomFeeder.initialize("100.11.12.13", "100.11.12.15", 3);
		assertEquals(0, randomFeeder.getPercentageComplete());
		randomFeeder.next();
		assertEquals(33, randomFeeder.getPercentageComplete());
		randomFeeder.next();
		assertEquals(67, randomFeeder.getPercentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.getPercentageComplete());
		
		randomFeeder.initialize("255.255.255.255", "255.255.255.255", 1);
		assertEquals(0, randomFeeder.getPercentageComplete());
		randomFeeder.next();
		assertEquals(100, randomFeeder.getPercentageComplete());
	}	

	public void testGetInfo() {
		RandomFeeder randomFeeder = new RandomFeeder();
		randomFeeder.initialize("100.11.12.13", "100.11.12.15", 3);
		assertEquals("100.11.12.13 / 100.11.12.15: 3", randomFeeder.getInfo());
		randomFeeder.initialize("0.0.0.0", "255.255.255.255", 129876);
		assertEquals("0.0.0.0 / 255.255.255.255: 129876", randomFeeder.getInfo());
	}
}
