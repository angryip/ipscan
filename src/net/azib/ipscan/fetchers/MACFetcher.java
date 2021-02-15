package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.gui.fetchers.MACFetcherPrefs;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MACFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac";
	static final Pattern macAddressPattern = Pattern.compile("([a-fA-F0-9]{1,2}[-:]){5}[a-fA-F0-9]{1,2}");
	static final Pattern leadingZeroesPattern = Pattern.compile("(?<=^|-|:)([A-F0-9])(?=-|:|$)");
	String separator = getPreferences().get("separator", ":");

	@Override public String getId() {
		return ID;
	}

	@Override public final String scan(ScanningSubject subject) {
		String mac = (String) subject.getParameter(ID);
		if (mac == null) mac = resolveMAC(subject.getAddress());
		subject.setParameter(ID, mac);
		return replaceSeparator(mac);
	}

	protected abstract String resolveMAC(InetAddress address);

	String bytesToMAC(byte[] bytes) {
		StringBuilder mac = new StringBuilder();
		for (byte b : bytes) mac.append(String.format("%02X", b)).append(":");
		if (mac.length() > 0) mac.deleteCharAt(mac.length()-1);
		return mac.toString();
	}

	String extractMAC(String line) {
		Matcher m = macAddressPattern.matcher(line);
		return m.find() ? addLeadingZeroes(m.group().toUpperCase()) : null;
	}

	String replaceSeparator(String mac) {
		return mac != null ? mac.replace(":", separator) : null;
	}

	private static String addLeadingZeroes(String mac) {
		return leadingZeroesPattern.matcher(mac).replaceAll("0$1");
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override public Class<? extends FetcherPrefs> getPreferencesClass() {
		return MACFetcherPrefs.class;
	}
}
