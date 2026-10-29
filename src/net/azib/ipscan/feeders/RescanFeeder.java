/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScanningSubject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * A Feeder for rescanning - takes a predefined list of IP addresses.
 * 
 * @author Anton Keks
 */
public class RescanFeeder extends AbstractFeeder {
	private Feeder originalFeeder;
	private List<InetAddress> addresses;

	int current;
	
	/**
	 * Initializes the RescanFeeder using the old feeder used for the real scan to delegate some calls to.
	 */
	public RescanFeeder(Feeder originalFeeder, String ... ips) {
		this.originalFeeder = originalFeeder;
		if (ips.length == 0)
			throw new IllegalArgumentException("no IP addresses specified");
		addresses = new ArrayList<>(ips.length);
		addAddresses(ips);
	}

	/**
	 * @return the label of the "old" feeder
	 */
	@Override public String getId() {
		return originalFeeder.getId();
	}
	
	@Override public String getName() {
		return Labels.getLabel("feeder.rescan.of") + originalFeeder.getName();
	}

	/**
	 * Appends more addresses to this rescan, so they are scanned too.
	 * Note: this is used while the rescan is still in progress.
	 * @param ips an array of IP addresses as Strings
	 * @return the number of addresses that were actually added
	 */
	public synchronized int addAddresses(String ... ips) {
		var added = 0;
		for (var s : ips) {
			try {
				var address = InetAddress.getByName(s);
				// don't add addresses that are still waiting to be scanned
				if (!isPending(address)) {
					addresses.add(address);
					added++;
				}
			}
			catch (UnknownHostException e) {
				throw new FeederException("malformedIP");
			}
		}
		return added;
	}

	private boolean isPending(InetAddress address) {
		for (var i = current; i < addresses.size(); i++) {
			if (addresses.get(i).equals(address)) return true;
		}
		return false;
	}

	@Override public synchronized boolean hasNext() {
		return current < addresses.size(); 
	}

	@Override public synchronized ScanningSubject next() {
		return originalFeeder.subject(addresses.get(current++));
	}

	@Override public synchronized int percentageComplete() {
		return current * 100 / addresses.size();
	}
	
	@Override public String getInfo() {
		return originalFeeder.getInfo();
	}

	@Override public boolean isLocalNetwork() {
		return originalFeeder.isLocalNetwork();
	}
}
