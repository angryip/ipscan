package net.azib.ipscan.fetchers;

import net.azib.ipscan.util.IOUtils;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UnixMACFetcher extends MACFetcher {
	@Inject public UnixMACFetcher() {}

	@Override public String resolveMAC(InetAddress address) {
		String ip = address.getHostAddress();
		BufferedReader reader = null;
		try {
			// highly inefficient implementation, there must be a better way (using JNA?)
			Process process = Runtime.getRuntime().exec("arp -an " + ip);
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
						if (addr.equals(address))
							return bytesToMAC(netif.getHardwareAddress());
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
}
