package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.SocketException;

public class UnixMACFetcher extends MACFetcher {
	private String arp;

	public UnixMACFetcher() {
		if (Platform.LINUX)
			arp = "arp -an "; // use BSD-style output
		else
			arp = "arp -n ";  // Mac and other BSD
	}

	@Override public String resolveMAC(ScanningSubject subject) {
		var ip = subject.getAddress().getHostAddress();
		BufferedReader reader = null;
		try {
			// highly inefficient implementation, there must be a better way (using JNA?)
			var process = Runtime.getRuntime().exec(arp + ip);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(ip))
					return extractMAC(line);
			}
			return getLocalMAC(subject);
		}
		catch (Exception e) {
			return null;
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}

	static String getLocalMAC(ScanningSubject subject) throws SocketException {
		return subject.isLocalHost() ? bytesToMAC(subject.getInterface().getHardwareAddress()) : null;
	}
}
