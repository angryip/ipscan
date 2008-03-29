/**
 * 
 */
package net.azib.ipscan.core;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * PortIteratorTest
 *
 * @author Anton Keks
 */
public class PortIteratorTest {

	@Test
	public void testBasic() {
		assertEquals("1 2 3 5 7 ", iterateToString(new PortIterator("1,2,3,5,7")));
		assertEquals("1 2 3 5 7 ", iterateToString(new PortIterator("1,\n2,   3,\t\t5,7")));
		assertEquals("27 1 65535 ", iterateToString(new PortIterator("27, 1;65535")));
		assertEquals("16 ", iterateToString(new PortIterator("16")));
		assertEquals("", iterateToString(new PortIterator("")));
		assertEquals("12 ", iterateToString(new PortIterator("   12")));
		assertEquals("12 ", iterateToString(new PortIterator("12, ")));
	}
	
	@Test
	public void testRange() {
		assertEquals("1 2 3 ", iterateToString(new PortIterator("1-3")));
		assertEquals("65530 65531 65532 65533 65534 65535 ", iterateToString(new PortIterator("65530-65535")));
		assertEquals("100 13 14 17 18 19 20 ", iterateToString(new PortIterator("100,13-14,17-20")));
	}
	
	@Test
	public void testSize() throws Exception {
		assertEquals(0, new PortIterator("").size());
		assertEquals(1, new PortIterator("80").size());
		assertEquals(5, new PortIterator("5,10-12,1").size());
		assertEquals(65000, new PortIterator("1-65000").size());
	}
	
	@Test
	public void testCopy() {
		assertNotNull(new PortIterator("1").copy());
	}
	
	@Test(expected=NumberFormatException.class)
	public void testBrokenNumber() throws Exception {
		new PortIterator("foo");
	}
	
	@Test(expected=NumberFormatException.class)
	public void testTooLarge() throws Exception {
		new PortIterator("65536");
	}
	
	@Test(expected=NumberFormatException.class)
	public void testZero() throws Exception {
		new PortIterator("1,2,0,3");
	}

	@Test(expected=NumberFormatException.class)
	public void testNegative() throws Exception {
		new PortIterator("-3");
	}

	private static String iterateToString(PortIterator iterator) {
		StringBuffer sb = new StringBuffer(64);
		while (iterator.hasNext()) {
			sb.append(iterator.next()).append(' ');
		}
		return sb.toString();
	}

}
