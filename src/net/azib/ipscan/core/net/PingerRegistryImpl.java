/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PingerRegistryImpl
 *
 * @author Anton Keks
 */
public class PingerRegistryImpl implements PingerRegistry {
	
	static final Logger LOG = Logger.getLogger(PingerRegistryImpl.class.getName());
	
	/** All available Pinger implementations */
	private Map<String, Class<? extends Pinger>> pingers;
	
	public PingerRegistryImpl() {
		pingers = new LinkedHashMap<String, Class<? extends Pinger>>();
		pingers.put("pinger.icmp", ICMPSharedPinger.class);
		pingers.put("pinger.icmp2", ICMPPinger.class);
		pingers.put("pinger.udp", UDPPinger.class);
		pingers.put("pinger.tcp", TCPPinger.class);
		
		// TODO: implement a windows-specific ICMP pinger for XP SP2 and beyond that uses ping.dll
		// TODO: autodetect working pingers here
	}
	
	public String[] getRegisteredNames() {
		return pingers.keySet().toArray(new String[pingers.size()]);
	}

	public Pinger createPinger(String pingerName, int timeout) {
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
			throw new RuntimeException(message);
		}
		
	}

}
