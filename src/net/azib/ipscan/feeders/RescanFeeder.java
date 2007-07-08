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
 * @author anton
 */
public class RescanFeeder implements Feeder {

	private List<InetAddress> addresses;

	int current;
	
	/**
	 * @see Feeder#getLabel()
	 */
	public String getLabel() {
		// this feeder is not a regular one
		return null;
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
		
	/**
	 * @see net.azib.ipscan.feeders.Feeder#hasNext()
	 */
	public boolean hasNext() {
		return current < addresses.size(); 
	}

	/**
	 * @see net.azib.ipscan.feeders.Feeder#next()
	 */
	public InetAddress next() {
		return addresses.get(current++);
	}

	public int percentageComplete() {
		return current * 100 / addresses.size();
	}
	
	/**
	 * @see net.azib.ipscan.feeders.Feeder#getInfo()
	 */
	public String getInfo() {
		// this is a non-standard feeder
		return null;
	}
}
