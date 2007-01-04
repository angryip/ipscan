/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Pinger
 *
 * @author anton
 */
public interface Pinger {

	/**
	 * Closes the raw socket opened by the constructor. After calling this
	 * method, the object cannot be used.
	 */
	public void close() throws IOException;

	/**
	 * Issues the specified number of pings and
	 * waits for replies.
	 * 
	 * @param count number of pings to perform
	 */
	public PingResult ping(InetAddress address, int count) throws IOException;

}
