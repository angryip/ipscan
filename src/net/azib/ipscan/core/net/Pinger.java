/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;

import java.io.Closeable;
import java.io.IOException;

/**
 * Pinger
 *
 * @author Anton Keks
 */
public interface Pinger extends Closeable {
	/**
	 * Issues the specified number of pings and
	 * waits for replies.
	 * 
	 * @param count number of pings to perform
	 */
	public PingResult ping(ScanningSubject subject, int count) throws IOException;

}
