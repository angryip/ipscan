package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnixMACFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.mac";
	static final Pattern macAddressPattern = Pattern.compile("([a-fA-F0-9]{1,2}(-|:)){5}[a-fA-F0-9]{1,2}");

	@Override public String getId() {
		return ID;
	}

	@Override public void init() {
	}

	@Override public String scan(ScanningSubject subject) {
		String ip = subject.getAddress().getHostAddress();
		BufferedReader reader = null;
		try {
			// highly inefficient implementation, there must be a better way (using JNA?)
			Process process = Runtime.getRuntime().exec("arp -an " + ip);
			process.waitFor();
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(ip))
					return extractMAC(line);
			}

			// see if it is a local address
			Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
			while (ifs.hasMoreElements()) {
				NetworkInterface netif = ifs.nextElement();
				if (netif.isUp() && !netif.isVirtual() && !netif.isLoopback()) {
					Enumeration<InetAddress> addrs = netif.getInetAddresses();
					while (addrs.hasMoreElements()) {
						InetAddress addr = addrs.nextElement();
						if (addr.equals(subject.getAddress()))
							return WinMACFetcher.bytesToMAC(netif.getHardwareAddress());
					}
				}
			}
			return null;
		}
		catch (Exception e) {
			return null;
		}
		finally {
			IOUtils.closeQuietly(reader);
		}
	}

	static String extractMAC(String line) {
		Matcher m = macAddressPattern.matcher(line);
		return m.find() ? m.group().toUpperCase() : null;
	}
}
