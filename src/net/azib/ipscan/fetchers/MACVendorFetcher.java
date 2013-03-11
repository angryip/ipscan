package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MACVendorFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac.vendor";
	private static Map<String, String> vendors = new HashMap<String, String>();

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void init() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/mac-vendors.txt")));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				int del = line.indexOf(' ');
				vendors.put(line.substring(0, del), line.substring(del+1));
			}
			IOUtils.closeQuietly(reader);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object scan(ScanningSubject subject) {
		String mac = (String)subject.getParameter(MACFetcher.ID);
		if (mac == null) return null;
		else return findMACVendor(mac);
	}

	String findMACVendor(String mac) {
		return vendors.get(mac.substring(0, 8));
	}
}
