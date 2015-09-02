/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.util;

import net.azib.ipscan.config.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * This class provides various utility static methods,
 * useful for transforming InetAddress objects.
 *
 * @author Anton Keks
 */
public class InetAddressUtils {
	
	static final Logger LOG = LoggerFactory.getLogger();
	
	// Warning! IPv4 specific code
	public static final Pattern IP_ADDRESS_REGEX = Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
	public static final Pattern HOSTNAME_REGEX = Pattern.compile("\\b(([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])\\.)+([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])\\b", CASE_INSENSITIVE);

	public static InetAddress startRangeByNetmask(InetAddress address, InetAddress netmask) {
		byte[] netmaskBytes = netmask.getAddress();
		byte[] addressBytes = address.getAddress();
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = (byte) (addressBytes[i] & netmaskBytes[i]);
		}
		try {
			return InetAddress.getByAddress(addressBytes);
		} 
		catch (UnknownHostException e) {
			// this should never happen as we are modifying the same bytes
			// received from the InetAddress
			return null;
		}
	}

	public static InetAddress endRangeByNetmask(InetAddress address, InetAddress netmask) {
		byte[] netmaskBytes = netmask.getAddress();
		byte[] addressBytes = address.getAddress();
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = (byte) (addressBytes[i] | ~(netmaskBytes[i]));
		}
		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException e) {
			// this should never happen as we are modifying the same bytes
			// received from the InetAddress
			return null;
		}
	}
	
	/**
	 * Compares two IP addresses.
	 * @return true in case inetAddress1 is greater than inetAddress2
	 */
	public static boolean greaterThan(InetAddress inetAddress1, InetAddress inetAddress2) {
		byte[] address1 = inetAddress1.getAddress();
		byte[] address2 = inetAddress2.getAddress();
		for (int i = 0; i < address1.length; i++) {
			if ((address1[i] & 0xFF) > (address2[i] & 0xFF))
				return true;
			else 
			if ((address1[i] & 0xFF) < (address2[i] & 0xFF))
				break;
		}
		return false;
	}
	
	/**
	 * Increments an IP address by 1.
	 */
	public static InetAddress increment(InetAddress address) {
		return modifyInetAddress(address, true);
	}

	/**
	 * Decrements an IP address by 1.
	 */
	public static InetAddress decrement(InetAddress address) {
		return modifyInetAddress(address, false);
	}

	/**
	 * Increments or decrements an IP address by 1.
	 * 
	 * @param address
	 *            the IP address
	 * @param isIncrement
	 * @return incremented/decremented IP address
	 */
	private static InetAddress modifyInetAddress(InetAddress address,
			boolean isIncrement) {
		try {
			byte[] newAddress = address.getAddress();
			for (int i = newAddress.length-1; i >= 0; i--) {
				if (isIncrement) {
					if (++newAddress[i] != 0x00) {
						break;
					}
				} else {
					if (--newAddress[i] != 0x00) {
						break;
					}
				}

			}
			return InetAddress.getByAddress(newAddress);
		}
		catch (UnknownHostException e) {
			// this exception is unexpected here
			assert false : e;
			return null;
		}
	}
	
	/**
	 * Parses the netmask string provided in special text format:
	 * A.B.C.D, where each term is 0-255 or empty. If any term is empty, it is the same as 255.
	 * Another supported format is CIDR ("/24").
	 * <p/>
	 * Only IPv4 is supported.
	 * 
	 * @param netmaskString
	 * @throws UnknownHostException
	 */
	public static InetAddress parseNetmask(String netmaskString) throws UnknownHostException {
		if (netmaskString.startsWith("/")) {
			// CIDR netmask, e.g. "/24" - number of bits set from the left
			int totalBits = Integer.parseInt(netmaskString.substring(1)); 
			byte[] mask = new byte[4]; // Warning: assume IPv4 here
			for (int i = 0; i < mask.length; i++) {
				int curByteBits = totalBits >= 8 ? 8 : totalBits;
				totalBits -= curByteBits;				
				mask[i] = (byte)((((1 << curByteBits)-1)<<(8-curByteBits)) & 0xFF); 
				
			}
			return InetAddress.getByAddress(mask);
		} 

		// IP-like netmask (IPv4)
		netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
		netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
		return InetAddress.getByName(netmaskString);
	}

	/**
	 * Where mask bits are set, we use prototype bits.
	 * Where mask bits are cleared, we leave bits as is.
	 * @param addressBytes this array is modified according to the maskBytes and prototypeBytes
	 * @param maskBytes
	 * @param prototypeBytes
	 */
	public static void maskPrototypeAddressBytes(byte[] addressBytes, byte[] maskBytes, byte[] prototypeBytes) {
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = (byte) ((addressBytes[i] & ~maskBytes[i]) | (prototypeBytes[i] & maskBytes[i]));
		}
	}

	/**
	 * Checks whether the passed address is likely either a broadcast or network address
	 * @param address
	 */
	public static boolean isLikelyBroadcast(InetAddress address) {
		byte[] bytes = address.getAddress(); 
		return bytes[bytes.length-1] == 0 || bytes[bytes.length-1] == (byte)0xFF;
	}

	public static InterfaceAddress getLocalInterface() {
		InterfaceAddress anyAddress = null;
		try {
			for (Enumeration<NetworkInterface> i = getNetworkInterfaces(); i.hasMoreElements(); ) {
				NetworkInterface networkInterface = i.nextElement();
				for (InterfaceAddress ifAddr : networkInterface.getInterfaceAddresses()) {
					anyAddress = ifAddr;
					InetAddress addr = ifAddr.getAddress();
					if (!addr.isLoopbackAddress() && addr instanceof Inet4Address)
						return ifAddr;
				}
			}
		}
		catch (SocketException e) {
			LOG.log(Level.FINE, "Cannot enumerate network interfaces", e);
		}
		return anyAddress;
	}
}
