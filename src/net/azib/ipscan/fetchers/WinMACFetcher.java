package net.azib.ipscan.fetchers;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.azib.ipscan.core.ScanningSubject;

import java.net.Inet4Address;

import static net.azib.ipscan.core.net.WinIpHlp.toIpAddr;
import static net.azib.ipscan.core.net.WinIpHlpDll.dll;

public class WinMACFetcher extends MACFetcher {
	@Override public String resolveMAC(ScanningSubject subject) {
		if (!(subject.getAddress() instanceof Inet4Address)) return null; // TODO IPv6 support

		Pointer pmac = new Memory(8);
		Pointer plen = new Memory(4);
		plen.setInt(0, 8);

		var result = dll.SendARP(toIpAddr(subject.getAddress()), 0, pmac, plen);

		if (result != 0) return null;

		var bytes = pmac.getByteArray(0, plen.getInt(0));
		return bytesToMAC(bytes);
	}
}
