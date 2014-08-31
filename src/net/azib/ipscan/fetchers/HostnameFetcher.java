/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.MDNSResolver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

/**
 * HostnameFetcher retrieves hostnames of IP addresses by reverse DNS lookups.
 * 
 * @author Anton Keks
 */
public class HostnameFetcher extends AbstractFetcher {
	private static Object inetAddressImpl;
	private static Method getHostByAddr;

	static {
		try {
			Field impl = InetAddress.class.getDeclaredField("impl");
			impl.setAccessible(true);
			inetAddressImpl = impl.get(null);
			getHostByAddr = inetAddressImpl.getClass().getDeclaredMethod("getHostByAddr", byte[].class);
			getHostByAddr.setAccessible(true);
		}
		catch (Exception e) {
			Logger.getLogger(HostnameFetcher.class.getName()).log(WARNING, "Could not get InetAddressImpl", e);
		}
	}

	public static final String ID = "fetcher.hostname";

	public String getId() {
		return ID;
	}

	private String resolveWithRegularDNS(InetAddress ip) {
		try {
			// faster way to do lookup - getCanonicalHostName() actually does both reverse and forward lookups inside
			return (String)getHostByAddr.invoke(inetAddressImpl, ip.getAddress());
		}
		catch (Exception e) {
			if (e instanceof InvocationTargetException && e.getCause() instanceof UnknownHostException)
				return null;

			// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
			String hostname = ip.getCanonicalHostName();
			return ip.getHostAddress().equals(hostname) ? null : hostname;
		}
	}

	private String resolveWithMulticastDNS(InetAddress ip) {
		try {
			MDNSResolver resolver = new MDNSResolver();
			String name = resolver.resolve(ip);
			resolver.close();
			return name;
		}
		catch (IOException e) {
			return null;
		}
	}

	public Object scan(ScanningSubject subject) {
		String name = resolveWithRegularDNS(subject.getAddress());
		if (name == null) name = resolveWithMulticastDNS(subject.getAddress());
		return name;
	}
}
