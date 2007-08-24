/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core.values;

import java.net.InetAddress;

/**
 * InetAddressValue - a comparable holder of IP addresses
 *
 * @author Anton Keks
 */
public class InetAddressValue implements Comparable<InetAddressValue> {
	
	private String s;
	private byte[] a;

	public InetAddressValue(InetAddress address) {
		s = address.getHostAddress();
		a = address.getAddress();
	}

	public int compareTo(InetAddressValue that) {
		byte[] b1 = this.a;
		byte[] b2 = that.a;
		
		// compare each byte
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] == b2[i])
				continue;
			else
			if (b1[i] > b2[i])
				return 1;
			else
				return -1;
		}
		// all bytes are equal
		return 0;
	}

	@Override
	public String toString() {
		return s;
	}
}
