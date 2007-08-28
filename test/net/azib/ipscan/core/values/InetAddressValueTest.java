/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.values;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Test;

/**
 * InetAddressValueTest
 *
 * @author Anton Keks
 */
public class InetAddressValueTest {
	@Test
	public void testToString() throws Exception {
		InetAddressValue av = new InetAddressValue(InetAddress.getLocalHost());
		assertEquals(InetAddress.getLocalHost().getHostAddress(), av.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		InetAddressValue av2 = new InetAddressValue(InetAddress.getByName("192.168.0.2"));
		InetAddressValue av10 = new InetAddressValue(InetAddress.getByName("192.168.0.10"));
		InetAddressValue av127 = new InetAddressValue(InetAddress.getByName("192.168.0.127"));
		InetAddressValue av253 = new InetAddressValue(InetAddress.getByName("192.168.0.253"));
		assertEquals(-1, av2.compareTo(av10));
		assertEquals(1, av10.compareTo(av2));
		assertEquals(0, av2.compareTo(av2));
		assertEquals(-1, av10.compareTo(av253));
		assertEquals(-1, av127.compareTo(av253));
		assertEquals(1, av253.compareTo(av127));
		assertEquals(1, av253.compareTo(av2));
		assertEquals(0, av253.compareTo(av253));
	}
	
	@Test
	public void testEqualsHashCode() throws Exception {
		InetAddressValue av1 = new InetAddressValue(InetAddress.getByName("192.168.0.2"));
		InetAddressValue av2 = new InetAddressValue(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, 0, 2}));
		assertEquals(av1, av2);
		assertEquals(av1.hashCode(), av2.hashCode());
		assertFalse(av1.equals(null));
		assertFalse(av1.equals(""));
	}
}
