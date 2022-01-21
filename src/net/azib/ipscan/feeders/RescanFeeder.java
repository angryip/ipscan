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
		initAddresses(ips);
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
	 * Initializes the RescanFeeder with required parameters
	 * @param ips an array of IP addresses as Strings
	 */
	private int initAddresses(String ... ips) {
		if (ips.length == 0)
			throw new IllegalArgumentException("no IP addresses specified");
		
		try {
			addresses = new ArrayList<>(ips.length);
			for (String s : ips) {
				addresses.add(InetAddress.getByName(s));
			}
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		return ips.length;
	}
		
	@Override public boolean hasNext() {
		return current < addresses.size(); 
	}

	@Override public ScanningSubject next() {
		return originalFeeder.subject(addresses.get(current++));
	}

	@Override public int percentageComplete() {
		return current * 100 / addresses.size();
	}
	
	@Override public String getInfo() {
		return originalFeeder.getInfo();
	}

	@Override public boolean isLocalNetwork() {
		return originalFeeder.isLocalNetwork();
	}
}
