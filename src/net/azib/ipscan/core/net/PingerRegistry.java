/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

/**
 * PingerRegistry
 *
 * @author Anton Keks Keks
 */
public interface PingerRegistry {

	/**
	 * @return a String array of pinger names (labels)
	 */
	public String[] getRegisteredNames();

	/**
	 * Creates a new instance of currently selected Pinger
	 * @param timeout
	 * @return the instance
	 */
	public Pinger createPinger(String pingerName, int timeout);

}
