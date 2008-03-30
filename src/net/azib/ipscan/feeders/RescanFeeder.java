/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

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

	private Feeder oldFeeder;
	private List<InetAddress> addresses;

	int current;
	
	/**
	 * Initializes the RescanFeeder using the old feeder used for the real scan to delegate some calls to.
	 * @param oldFeeder
	 */
	public RescanFeeder(Feeder oldFeeder) {
		this.oldFeeder = oldFeeder;
	}

	/**
	 * @return the label of the "old" feeder
	 */
	public String getId() {
		return oldFeeder.getId();
	}

	/**
	 * Initializes the RescanFeeder with required parameters
	 * @see Feeder#initialize(String[])
	 * @param params an array of IP addresses as Strings
	 */
	public int initialize(String ... params) {
		if (params.length == 0)
			throw new IllegalArgumentException("no IP addresses specified");
		
		try {
			addresses = new ArrayList<InetAddress>(params.length);
			for (String s : params) {
				addresses.add(InetAddress.getByName(s));
			}
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		return params.length;
	}
		
	public boolean hasNext() {
		return current < addresses.size(); 
	}

	public InetAddress next() {
		return addresses.get(current++);
	}

	public int percentageComplete() {
		return current * 100 / addresses.size();
	}
	
	/**
	 * @return the info of the "old" feeder
	 */
	public String getInfo() {
		return oldFeeder.getInfo();
	}
}
