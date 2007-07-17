/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.fetchers.FetcherException;

/**
 * PingerRegistryImpl
 *
 * @author Anton Keks Keks
 */
public class PingerRegistryImpl implements PingerRegistry {
	
	private static final Logger LOG = Logger.getLogger(PingerRegistryImpl.class.getName());
	
	private GlobalConfig globalConfig;
	
	/** All available Pinger implementations */
	Map<String, Class<? extends Pinger>> pingers;
	
	public PingerRegistryImpl(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
		
		pingers = new LinkedHashMap<String, Class<? extends Pinger>>();
		pingers.put("pinger.icmp", ICMPSharedPinger.class);
		pingers.put("pinger.icmp2", ICMPPinger.class);
		pingers.put("pinger.udp", UDPPinger.class);
		pingers.put("pinger.tcp", TCPPinger.class);
		// TODO: implement a windows-specific ICMP pinger for XP SP2 and beyond that uses ping.dll
	}
	
	public String[] getRegisteredNames() {
		return pingers.keySet().toArray(new String[pingers.size()]);
	}
	
	/**
	 * Creates the configured pinger with configured timeout
	 */
	public Pinger createPinger() throws FetcherException {
		return createPinger(globalConfig.selectedPinger, globalConfig.pingTimeout);
	}

	/**
	 * Creates a specified pinger with specified timeout
	 */
	Pinger createPinger(String pingerName, int timeout) throws FetcherException {
		Class<? extends Pinger> pingerClass = pingers.get(pingerName);
		Constructor<? extends Pinger> constructor;
		try {
			constructor = pingerClass.getConstructor(new Class[] {int.class});
			return constructor.newInstance(new Object[] {new Integer(timeout)});
		}
		catch (Exception e) {
			Throwable t = e instanceof InvocationTargetException ? e.getCause() : e; 
			String message = "Unable to create pinger: " + pingerName;
			LOG.log(Level.SEVERE, message, t);
			if (t instanceof RuntimeException)
				throw (RuntimeException) t;
			throw new FetcherException("pingerCreateFailure");
		}
	}

	public boolean checkSelectedPinger() {
		// this method must be fast, so we are not checking all the implementations		
		// currently only icmp pingers may not be supported, so let's check them
		if (globalConfig.selectedPinger.startsWith("pinger.icmp")) {
			try {
				Pinger icmpPinger = createPinger(globalConfig.selectedPinger, 250);
				icmpPinger.ping(InetAddress.getLocalHost(), 1);
			}
			catch (Exception e) {
				LOG.info("ICMP pingers fail: " + e);
				// udp should be supported in all configurations
				globalConfig.selectedPinger = "pinger.udp";
				return false;
			}
		}
		return true;
	}

}
