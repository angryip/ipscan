/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.MDNSResolver;
import net.azib.ipscan.util.NetBIOSResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;

/**
 * HostnameFetcher retrieves hostnames of IP addresses by reverse DNS lookups.
 * 
 * @author Anton Keks
 */
public class HostnameFetcher extends AbstractFetcher {
	private static final Logger LOG = LoggerFactory.getLogger();

	private static Object inetAddressImpl;
	private static Method getHostByAddr;

	static {
		try {
			var impl = InetAddress.class.getDeclaredField("impl");
			impl.setAccessible(true);
			inetAddressImpl = impl.get(null);
			getHostByAddr = inetAddressImpl.getClass().getDeclaredMethod("getHostByAddr", byte[].class);
			getHostByAddr.setAccessible(true);
		}
		catch (Exception e) {
			LOG.log(WARNING, "Could not get InetAddressImpl", e);
		}
	}

	public static final String ID = "fetcher.hostname";

	public String getId() {
		return ID;
	}

	@SuppressWarnings("PrimitiveArrayArgumentToVariableArgMethod")
	private String resolveWithRegularDNS(InetAddress ip) {
		String hostname = null;
		if (getHostByAddr != null) {
			try {
				hostname = (String) getHostByAddr.invoke(inetAddressImpl, ip.getAddress());
			}
			catch (InvocationTargetException e) {
				if (e.getCause() instanceof UnknownHostException) return null;
			}
			catch (Exception e) {
				LOG.log(FINE, "Reflection-based DNS lookup failed", e);
			}
		}
		if (hostname == null) {
			// fallback: getCanonicalHostName() also does a forward lookup, so it's slower
			var canonical = ip.getCanonicalHostName();
			hostname = ip.getHostAddress().equals(canonical) ? null : canonical;
		}
		// verify forward lookup to prevent DNS rebinding attacks
		if (hostname != null) {
			try {
				if (!InetAddress.getByName(hostname).equals(ip)) return null;
			}
			catch (Exception e) {
				return null;
			}
		}
		return unescapeDNSName(hostname);
	}

	private static final Pattern DNS_ESCAPE_PATTERN = Pattern.compile("\\\\(\\d{3})");

	/**
	 * Some routers/DNS servers incorrectly send raw zone-file escape sequences
	 * (RFC 1035 presentation format, e.g. "\032" for a space) as literal bytes
	 * in PTR records, instead of the actual characters they represent.
	 * This decodes such "\DDD" decimal escape sequences back into their original characters.
	 */
	static String unescapeDNSName(String hostname) {
		if (hostname == null || hostname.indexOf('\\') < 0) return hostname;
		var matcher = DNS_ESCAPE_PATTERN.matcher(hostname);
		var result = new StringBuilder();
		while (matcher.find()) {
			int code = Integer.parseInt(matcher.group(1));
			var replacement = code <= 255 ? String.valueOf((char) code) : matcher.group();
			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}
		matcher.appendTail(result);
		return result.toString();
	}

	private String resolveWithMulticastDNS(ScanningSubject subject) {
		try (var resolver = new MDNSResolver(subject.getAdaptedPortTimeout())) {
			return resolver.resolve(subject.getAddress());
		}
		catch (SocketTimeoutException | SocketException e) {
			return null;
		}
		catch (Exception e) {
			LOG.log(WARNING, "Failed to query mDNS for " + subject, e);
			return null;
		}
	}

	private String resolveWithNetBIOS(ScanningSubject subject) {
		try (var resolver = new NetBIOSResolver(subject.getAdaptedPortTimeout())) {
			var names = resolver.resolve(subject.getAddress());
			return names == null ? null : names[0];
		}
		catch (SocketTimeoutException | SocketException e) {
			return null;
		}
		catch (Exception e) {
			LOG.log(WARNING, "Failed to query NetBIOS for " + subject, e);
			return null;
		}
	}

	public Object scan(ScanningSubject subject) {
		var name = resolveWithRegularDNS(subject.getAddress());
		if (name == null && subject.isLocal()) name = resolveWithMulticastDNS(subject);
		if (name == null && subject.isLocal()) name = resolveWithNetBIOS(subject);
		return name;
	}
}
