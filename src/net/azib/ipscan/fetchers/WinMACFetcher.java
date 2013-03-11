package net.azib.ipscan.fetchers;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.azib.ipscan.core.net.WinIpHlpDll;
import net.azib.ipscan.core.net.WinIpHlpDll.IpAddrByVal;

import java.net.InetAddress;

public class WinMACFetcher extends MACFetcher {
	private WinIpHlpDll dll;

	@Override public void init() {
		dll = WinIpHlpDll.Loader.load();
	}

	@Override public String resolveMAC(InetAddress address) {
		IpAddrByVal destIP = new IpAddrByVal();
		destIP.bytes = address.getAddress();

		Pointer pmac = new Memory(8);
		Pointer plen = new Memory(4);
		plen.setInt(0, 8);

		int result = dll.SendARP(destIP, 0, pmac, plen);

		if (result != 0) return null;

		byte[] bytes = pmac.getByteArray(0, plen.getInt(0));
		return bytesToMAC(bytes);
	}
}
