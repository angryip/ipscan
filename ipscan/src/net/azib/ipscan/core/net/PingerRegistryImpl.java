/**
 * 
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
	
	private static final Logger LOG = Logger.getLogger(PingerRegistryImpl.class.getName());
	
	/** All available Pinger implementations */
	private Map pingers;
	
	public PingerRegistryImpl() {
		pingers = new LinkedHashMap();
		pingers.put("pinger.icmp", ICMPSharedPinger.class);
		pingers.put("pinger.icmp2", ICMPPinger.class);
		pingers.put("pinger.udp", UDPPinger.class);
		pingers.put("pinger.tcp", TCPPinger.class);
		
		// TODO: implement a windows-specific ICMP pinger for XP SP2 and beyond that uses ping.dll
		// TODO: autodetect working pingers here
	}
	
	public String[] getRegisteredNames() {
		return (String[]) pingers.keySet().toArray(new String[pingers.size()]);
	}

	public Pinger createPinger(String pingerName, int timeout) {
		Class pingerClass = (Class) pingers.get(pingerName);
		Constructor constructor;
		try {
			constructor = pingerClass.getConstructor(new Class[] {int.class});
			return (Pinger) constructor.newInstance(new Object[] {new Integer(timeout)});
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
