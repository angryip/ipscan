package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.WinIpHlpDll;
import net.azib.ipscan.core.net.WinIpHlpDll.*;
import net.azib.ipscan.fetchers.AbstractFetcher;

import java.lang.*;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;

import static java.lang.Integer.toHexString;

public class WinMACFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac";

	private WinIpHlpDll dll;

	@Override public String getId() {
		return ID;
	}

	@Override public void init() {
		dll = WinIpHlpDll.Loader.load();
	}

	@Override public String scan(ScanningSubject subject) {
		IpAddrByVal destIP = new IpAddrByVal();
		destIP.bytes = subject.getAddress().getAddress();

		Pointer pmac = new Memory(8);
		Pointer plen = new Memory(4);
		plen.setInt(0, 8);

		int result = dll.SendARP(destIP, 0, pmac, plen);

		if (result != 0) return null;

		byte[] bytes = pmac.getByteArray(0, plen.getInt(0));
		return bytesToMAC(bytes);
	}

	static String bytesToMAC(byte[] bytes) {
		StringBuilder mac = new StringBuilder();
		for (byte b : bytes) mac.append(String.format("%02X", b)).append(":");
		if (mac.length() > 0) mac.deleteCharAt(mac.length()-1);
		return mac.toString();
	}
}
