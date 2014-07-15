/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;

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

	public Object scan(ScanningSubject subject) {
		try {
			// faster way to do lookup - getCanonicalHostName() actually does both reverse and forward lookups inside
			return getHostByAddr.invoke(inetAddressImpl, subject.getAddress().getAddress());
		}
		catch (Exception e) {
			if (e instanceof InvocationTargetException && e.getCause() instanceof UnknownHostException)
				return null;

			// return the returned hostname only if it is not the same as the IP address (this is how the above method works)
			String hostname = subject.getAddress().getCanonicalHostName();
			return subject.getAddress().getHostAddress().equals(hostname) ? null : hostname;
		}
	}

}
