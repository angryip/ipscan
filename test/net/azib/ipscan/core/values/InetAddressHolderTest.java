/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.values;

import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * InetAddressValueTest
 *
 * @author Anton Keks
 */
public class InetAddressHolderTest {
	@Test
	public void testToString() throws Exception {
		var av = new InetAddressHolder(InetAddress.getLocalHost());
		assertEquals(InetAddress.getLocalHost().getHostAddress(), av.toString());
	}
	
	@Test
	public void testCompareTo() throws Exception {
		var av2 = new InetAddressHolder(InetAddress.getByName("192.168.0.2"));
		var av10 = new InetAddressHolder(InetAddress.getByName("192.168.0.10"));
		var av127 = new InetAddressHolder(InetAddress.getByName("192.168.0.127"));
		var av253 = new InetAddressHolder(InetAddress.getByName("192.168.0.253"));
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
		var av1 = new InetAddressHolder(InetAddress.getByName("192.168.0.2"));
		var av2 = new InetAddressHolder(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, 0, 2}));
		assertEquals(av1, av2);
		assertEquals(av1.hashCode(), av2.hashCode());
		assertFalse(av1.equals(null));
		assertFalse(av1.equals(""));
	}
}
