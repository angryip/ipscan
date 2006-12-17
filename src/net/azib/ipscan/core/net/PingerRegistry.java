/**
 * 
 */
package net.azib.ipscan.core.net;

/**
 * PingerRegistry
 *
 * @author Anton Keks
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
