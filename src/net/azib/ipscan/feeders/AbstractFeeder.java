/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import net.azib.ipscan.core.ScanningSubject;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

import static net.azib.ipscan.util.InetAddressUtils.getInterface;
import static net.azib.ipscan.util.InetAddressUtils.matchingAddress;

/**
 * Helper base class for built-in Feeders
 *
 * @author Anton Keks
 */
public abstract class AbstractFeeder implements Feeder {
	private NetworkInterface netIf;
	private InterfaceAddress ifAddr;

	protected void initInterfaces(InetAddress ip) {
		this.netIf = getInterface(ip);
		this.ifAddr = matchingAddress(netIf, ip.getClass());
	}

	@Override public ScanningSubject subject(InetAddress ip) {
		return new ScanningSubject(ip, netIf, ifAddr);
	}

	@Override public boolean isLocalNetwork() {
		return ifAddr != null;
	}

	@Override public String toString() {
		return getName() + ": " + getInfo();
	}
}
