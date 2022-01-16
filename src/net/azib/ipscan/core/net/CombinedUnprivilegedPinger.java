/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.core.net;

import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;

import static java.lang.Math.max;

/**
 * CombinedUnprivilegedPinger - uses both UDP and TCP for pinging.
 * A better default alternative for unprivileged users.
 *
 * @author Anton Keks
 */
public class CombinedUnprivilegedPinger implements Pinger {
	private TCPPinger tcpPinger;
	private UDPPinger udpPinger;

	public CombinedUnprivilegedPinger(TCPPinger tcpPinger, UDPPinger udpPinger) {
		this.tcpPinger = tcpPinger;
		this.udpPinger = udpPinger;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		// try UDP first - it should be more reliable in general
		int udpCountInitialCount = max(1, count / 2);
		PingResult udpResult = udpPinger.ping(subject, udpCountInitialCount);
		if (udpResult.isAlive())
			return udpResult.merge(udpPinger.ping(subject, count - udpCountInitialCount));

		// fallback to TCP - it may detect some hosts UDP cannot
		PingResult tcpResult = tcpPinger.ping(subject, count);
		return tcpResult.merge(udpResult);
	}
}
