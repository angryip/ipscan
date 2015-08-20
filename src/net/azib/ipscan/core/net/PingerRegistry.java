/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.FetcherException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

/**
 * PingerRegistry
 *
 * @author Anton Keks
 */
@Singleton
public class PingerRegistry {
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private ScannerConfig scannerConfig;
	
	/** All available Pinger implementations */
	Map<String, Class<? extends Pinger>> pingers;

	@Inject public PingerRegistry(ScannerConfig scannerConfig) {
		this.scannerConfig = scannerConfig;
		
		pingers = new LinkedHashMap<String, Class<? extends Pinger>>();
		if (Platform.WINDOWS)
			pingers.put("pinger.windows", WindowsPinger.class);
		pingers.put("pinger.icmp", ICMPSharedPinger.class);
		pingers.put("pinger.icmp2", ICMPPinger.class);
		pingers.put("pinger.udp", UDPPinger.class);
		pingers.put("pinger.tcp", TCPPinger.class);
		pingers.put("pinger.combined", CombinedUnprivilegedPinger.class);
	}

	public String[] getRegisteredNames() {
		return pingers.keySet().toArray(new String[pingers.size()]);
	}
	
	/**
	 * Creates the configured pinger with configured timeout
	 */
	public Pinger createPinger() throws FetcherException {
		return createPinger(scannerConfig.selectedPinger, scannerConfig.pingTimeout);
	}

	/**
	 * Creates a specified pinger with specified timeout
	 */
	Pinger createPinger(String pingerName, int timeout) throws FetcherException {
		Class<? extends Pinger> pingerClass = pingers.get(pingerName);
		Constructor<? extends Pinger> constructor;
		try {
			constructor = pingerClass.getConstructor(int.class);
			return constructor.newInstance(timeout);
		}
		catch (Exception e) {
			Throwable t = e instanceof InvocationTargetException ? e.getCause() : e; 
			String message = "Unable to create pinger: " + pingerName;
			LOG.log(SEVERE, message, t);
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new FetcherException("pingerCreateFailure");
		}
	}

	public boolean checkSelectedPinger() {
		// this method must be fast, so we are not checking all the implementations		
		// currently only icmp pingers may not be supported, so let's check them
		if (scannerConfig.selectedPinger.startsWith("pinger.icmp")) {
			try {
				Pinger icmpPinger = createPinger(scannerConfig.selectedPinger, 250);
				icmpPinger.ping(new ScanningSubject(InetAddress.getLocalHost()), 1);
			}
			catch (Throwable e) {
				LOG.info("ICMP pinger failed: " + e);
				// win32 will use native pinger, all others get combined UDP+TCP, which doesn't require special privileges
				scannerConfig.selectedPinger = Platform.WINDOWS ? "pinger.windows" : "pinger.combined";
				return false;
			}
		}
		return true;
	}
}
