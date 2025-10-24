/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.util;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Platform;

import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.list;
import static java.util.Collections.reverse;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.toList;

/**
 * This class provides various utility static methods,
 * useful for transforming InetAddress objects.
 *
 * @author Anton Keks
 */
public class InetAddressUtils {
	static final Logger LOG = LoggerFactory.getLogger();

	public static final Pattern HOSTNAME_REGEX = Pattern.compile("(\\b(((?!25?[6-9])[12]\\d|[1-9])?\\d\\.?\\b){4}\\b)|(?<!(\\w|:))(([0-9a-f]{1,4}:){7,7}[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,7}:|([0-9a-f]{1,4}:){1,6}:[0-9a-f]{1,4}|([0-9a-f]{1,4}:){1,5}(:[0-9a-f]{1,4}){1,2}|([0-9a-f]{1,4}:){1,4}(:[0-9a-f]{1,4}){1,3}|([0-9a-f]{1,4}:){1,3}(:[0-9a-f]{1,4}){1,4}|([0-9a-f]{1,4}:){1,2}(:[0-9a-f]{1,4}){1,5}|[0-9a-f]{1,4}:((:[0-9a-f]{1,4}){1,6})|:((:[0-9a-f]{1,4}){1,7}|:)|fe80:(:[0-9a-f]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-f]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))(?!(\\w|:))|(\\b(([a-z]|[a-z0-9][a-z0-9\\-]*[a-z0-9])\\.)+([a-z0-9]{2,})\\.?(?!(\\w|\\.)))", CASE_INSENSITIVE);

	public static InetAddress startRangeByNetmask(InetAddress address, InetAddress netmask) {
		byte[] addressBytes = address.getAddress();
		byte[] netmaskBytes = netmask.getAddress();
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = i < netmaskBytes.length ? (byte) (addressBytes[i] & netmaskBytes[i]) : 0;
		}
		try {
			return InetAddress.getByAddress(addressBytes);
		}
		catch (UnknownHostException e) {
			// this should never happen as we are modifying the same bytes received from the InetAddress
			throw new IllegalArgumentException(e);
		}
	}

	public static InetAddress endRangeByNetmask(InetAddress address, InetAddress netmask) {
		byte[] netmaskBytes = netmask.getAddress();
		byte[] addressBytes = address.getAddress();
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = (byte) (i < netmaskBytes.length ? (addressBytes[i] | ~(netmaskBytes[i])) : 255);
		}
		try {
			return InetAddress.getByAddress(addressBytes);
		}
		catch (UnknownHostException e) {
			// this should never happen as we are modifying the same bytes received from the InetAddress
			throw new IllegalArgumentException(e);
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

	public static InetAddress increment(InetAddress address) {
		return modifyInetAddress(address, true);
	}

	public static InetAddress decrement(InetAddress address) {
		return modifyInetAddress(address, false);
	}

	/**
	 * Increments or decrements an IP address by 1.
	 * @return incremented/decremented IP address
	 */
	private static InetAddress modifyInetAddress(InetAddress address, boolean isIncrement) {
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
	 */
	public static InetAddress parseNetmask(String netmaskString) throws UnknownHostException {
		if (netmaskString.startsWith("/")) {
			// CIDR netmask, e.g. "/24" - number of bits set from the left
			int totalBits = Integer.parseInt(netmaskString.substring(1));
			return parseNetmask(totalBits);
		}

		// IP-like netmask (IPv4)
		netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
		netmaskString = netmaskString.replaceAll("\\.\\.", ".255.");
		return InetAddress.getByName(netmaskString);
	}

	public static InetAddress parseNetmask(int prefixBits) {
		byte[] mask = new byte[prefixBits > 32 ? 16 : 4];
		for (int i = 0; i < mask.length; i++) {
			int curByteBits = Math.min(prefixBits, 8);
			prefixBits -= curByteBits;
			mask[i] = (byte)((((1 << curByteBits)-1)<<(8-curByteBits)) & 0xFF);
		}
		try {
			return InetAddress.getByAddress(mask);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Where mask bits are set, we use prototype bits.
	 * Where mask bits are cleared, we leave bits as is.
	 * @param addressBytes this array is modified according to the maskBytes and prototypeBytes
	 */
	public static void maskPrototypeAddressBytes(byte[] addressBytes, byte[] maskBytes, byte[] prototypeBytes) {
		for (int i = 0; i < addressBytes.length; i++) {
			addressBytes[i] = (byte) ((addressBytes[i] & ~maskBytes[i]) | (prototypeBytes[i] & maskBytes[i]));
		}
	}

	/**
	 * Checks whether the passed address is likely either a broadcast or network address
	 */
	public static boolean isLikelyBroadcast(InetAddress address, InterfaceAddress ifAddr) {
		byte[] bytes = address.getAddress();
		int last = bytes.length - 1;
		if (ifAddr != null) {
			return address.equals(ifAddr.getBroadcast()) ||
					// TODO: 0 is actually not correct for smaller networks than /24
					bytes[last] == 0 && Arrays.equals(bytes, 0, last, ifAddr.getAddress().getAddress(), 0, last);
		}
		return bytes[last] == 0 || bytes[last] == (byte)0xFF;
	}

	public static InterfaceAddress getLocalInterface() {
		InterfaceAddress anyAddress = null;
		try {
			List<NetworkInterface> interfaces = getNetworkInterfaces().stream()
					.filter(i -> i.getParent() == null && !i.isVirtual()).collect(toList());

			for (NetworkInterface networkInterface : interfaces) {
				try {
					if (networkInterface.getHardwareAddress() == null) continue;
				} catch (SocketException ignore) {}

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

	public static NetworkInterface getInterface(InterfaceAddress address) {
		try {
			if (address == null) return null;
			return NetworkInterface.getByInetAddress(address.getAddress());
		}
		catch (SocketException e) {
			return null;
		}
	}

	public static NetworkInterface getInterface(InetAddress address, Stream<NetworkInterface> interfaceStream) {
		try {
			if (address == null) return null;
			return interfaceStream.filter(i -> i.getInterfaceAddresses().stream().anyMatch(ifAddr -> {
				InetAddress netmask = parseNetmask(ifAddr.getNetworkPrefixLength());
				return startRangeByNetmask(address, netmask).equals(startRangeByNetmask(ifAddr.getAddress(), netmask));
			})).findFirst().orElse(null);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static NetworkInterface getInterface(InetAddress address) {
		try {
			return getInterface(address, NetworkInterface.networkInterfaces());
		}
		catch (Exception e) {
			return null;
		}
	}

	public static InterfaceAddress matchingAddress(NetworkInterface netIf, Class<? extends InetAddress> addressClass) {
		if (netIf == null) return null;
		return netIf.getInterfaceAddresses().stream().filter(i -> i.getAddress().getClass() == addressClass).findFirst().orElse(null);
	}

	public static List<NetworkInterface> getNetworkInterfaces() throws SocketException {
		List<NetworkInterface> interfaces = list(NetworkInterface.getNetworkInterfaces());
		if (!Platform.WINDOWS) reverse(interfaces);
		return interfaces;
	}
}
