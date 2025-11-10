package net.azib.ipscan.core.net;

import net.azib.ipscan.core.net.WinIpHlpDll.Ip6SockAddrByRef;
import net.azib.ipscan.core.net.WinIpHlpDll.IpAddrByVal;

import java.net.InetAddress;

public class WinIpHlp {
	public static IpAddrByVal toIpAddr(InetAddress address) {
		var addr = new IpAddrByVal();
		addr.bytes = address.getAddress();
		return addr;
	}

	public static Ip6SockAddrByRef toIp6Addr(InetAddress address) {
		var addr = new Ip6SockAddrByRef();
		addr.bytes = address.getAddress();
		return addr;
	}
}
