package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MACVendorFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac.vendor";
	private static Map<String, String> vendors = new HashMap<>();
	private MACFetcher macFetcher;

	public MACVendorFetcher(MACFetcher macFetcher) {
		this.macFetcher = macFetcher;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void init() {
		try (var reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/mac-vendors.txt")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				vendors.put(line.substring(0, 6), line.substring(6));
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object scan(ScanningSubject subject) {
		var mac = (String)subject.getParameter(MACFetcher.ID);
		if (mac == null) {
			macFetcher.scan(subject);
			mac = (String) subject.getParameter(MACFetcher.ID);
		}
		return mac != null ? findMACVendor(mac) : null;
	}

	String findMACVendor(String mac) {
		return vendors.get(mac.replace(":", "").substring(0, 6));
	}
}
