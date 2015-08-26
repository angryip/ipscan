package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MACFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac";
	static final Pattern macAddressPattern = Pattern.compile("([a-fA-F0-9]{1,2}(-|:)){5}[a-fA-F0-9]{1,2}");
	static final Pattern leadingZeroesPattern = Pattern.compile("(?<=^|-|:)([A-F0-9])(?=-|:|$)");

	@Override public String getId() {
		return ID;
	}

	@Override
	public final String scan(ScanningSubject subject) {
		String mac = (String) subject.getParameter(ID);
		if (mac == null) mac = resolveMAC(subject.getAddress());
		subject.setParameter(ID, mac);
		return mac;
	}

	protected abstract String resolveMAC(InetAddress address);

	static String bytesToMAC(byte[] bytes) {
		StringBuilder mac = new StringBuilder();
		for (byte b : bytes) mac.append(String.format("%02X", b)).append(":");
		if (mac.length() > 0) mac.deleteCharAt(mac.length()-1);
		return mac.toString();
	}

	static String extractMAC(String line) {
		Matcher m = macAddressPattern.matcher(line);
		return m.find() ? addLeadingZeroes(m.group().toUpperCase()) : null;
	}

	private static String addLeadingZeroes(String mac) {
		return leadingZeroesPattern.matcher(mac).replaceAll("0$1");
	}
}
