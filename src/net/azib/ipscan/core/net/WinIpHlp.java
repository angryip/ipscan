package net.azib.ipscan.core.net;

import net.azib.ipscan.core.net.WinIpHlpDll.Ip6SockAddrByRef;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import static java.nio.ByteOrder.nativeOrder;

public class WinIpHlp {
	/**
	 * @return IPAddr, which is a 32-bit integer holding address bytes in network order
	 */
	public static int toIpAddr(InetAddress address) {
		return ByteBuffer.wrap(address.getAddress()).order(nativeOrder()).getInt();
	}

	public static Ip6SockAddrByRef toIp6Addr(InetAddress address) {
		var addr = new Ip6SockAddrByRef();
		addr.bytes = address.getAddress();
		if (address instanceof Inet6Address inet6)
			addr.scopeId = inet6.getScopeId();
		return addr;
	}
}
