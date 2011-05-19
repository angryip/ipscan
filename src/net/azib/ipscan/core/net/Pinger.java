/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;

import net.azib.ipscan.core.ScanningSubject;

/**
 * Pinger
 *
 * @author Anton Keks
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
	public PingResult ping(ScanningSubject subject, int count) throws IOException;

}
