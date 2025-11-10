/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.core.Plugin;
import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;

/**
 * Pingers check if hosts are alive
 *
 * @author Anton Keks
 */
public interface Pinger extends Plugin, AutoCloseable {
	/**
	 * Issues the specified number of pings and
	 * waits for replies.
	 * 
	 * @param count number of pings to perform
	 */
	PingResult ping(ScanningSubject subject, int count) throws IOException;

	@Override default void close() throws IOException {}

	@Override default String getId() {
		return "pinger." + getClass().getSimpleName().replace("Pinger", "").toLowerCase();
	}
}
