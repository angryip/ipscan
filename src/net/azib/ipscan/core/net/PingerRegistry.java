/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.fetchers.FetcherException;

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
	 * @return the instance
	 * @throws FetcherException in case the pinger cannot be created
	 */
	public Pinger createPinger() throws FetcherException;
	
	/**
	 * Checks that the currently configured pinger is supported.
	 * If not, then another approperiate pinger is selected.
	 * @return false if not supported.
	 */
	public boolean checkSelectedPinger();

}
