/**
 * 
 */
package net.azib.ipscan.core;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * PortIteratorTest
 *
 * @author anton
 */
public class PortIteratorTest {

	@Test
	public void testBasic() {
		assertEquals("1 2 3 5 7 ", iterateToString(new PortIterator("1,2,3,5,7")));
		assertEquals("27 1 65535 ", iterateToString(new PortIterator("27, 1;65535")));
		assertEquals("16 ", iterateToString(new PortIterator("16")));
		assertEquals("", iterateToString(new PortIterator("")));
		assertEquals("12 ", iterateToString(new PortIterator("   12")));
		assertEquals("12 ", iterateToString(new PortIterator("12, ")));
		// TODO assertEquals("", iterateToString(new PortIterator("65536")));
	}
	
	@Test
	public void testRange() {
		assertEquals("1 2 3 ", iterateToString(new PortIterator("1-3")));
		assertEquals("65530 65531 65532 65533 65534 65535 ", iterateToString(new PortIterator("65530-65535")));
		assertEquals("100 13 14 17 18 19 20 ", iterateToString(new PortIterator("100,13-14,17-20")));
	}
	
	@Test
	public void testCopy() {
		assertNotNull(new PortIterator("1").copy());
	}
	
	private static String iterateToString(PortIterator iterator) {
		StringBuffer sb = new StringBuffer(64);
		while (iterator.hasNext()) {
			sb.append(iterator.next()).append(' ');
		}
		return sb.toString();
	}

}
