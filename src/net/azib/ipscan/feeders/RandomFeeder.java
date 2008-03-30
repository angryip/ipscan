/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import net.azib.ipscan.core.InetAddressUtils;

/**
 * A feeder, that generates random IP addresses.
 *
 * @author Anton Keks
 */
public class RandomFeeder implements Feeder {
	
	SecureRandom random = new SecureRandom();
	InetAddress currentAddress;
	
	byte[] prototypeBytes;
	byte[] maskBytes;
	byte[] currentBytes;
	
	int addressCount;
	int currentNumber;

	/**
	 * @see Feeder#getId()
	 */
	public String getId() {
		return "feeder.random";
	}

	/**
	 * Initializes the RandomFeeder with required parameters
	 * @see Feeder#initialize(String[])
	 * @param params 3 parameters:
	 * 		params[0] prototypeIP
	 * 		params[1] mask
	 * 		params[2] count
	 */
	public int initialize(String ... params) {
		try {
			initialize(params[0], params[1], Integer.parseInt(params[2]));
			return 3;
		}
		catch (NumberFormatException e) {
			throw new FeederException("random.invalidCount");
		}
	}
	
	public void initialize(String prototypeIP, String mask, int count) {
		try {
			this.prototypeBytes = InetAddress.getByName(prototypeIP).getAddress();
		} 
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		
		try {
			this.maskBytes = InetAddressUtils.parseNetmask(mask).getAddress();
		} 
		catch (UnknownHostException e) {
			throw new FeederException("invalidNetmask");
		}
		
		if (count <= 0) {
			throw new FeederException("random.invalidCount");
		}
		
		this.currentNumber = 0;
		this.addressCount = count;
		this.currentBytes = new byte[prototypeBytes.length];
	}

	public int percentageComplete() {
		return Math.round((float)currentNumber * 100 / addressCount);
	}

	public boolean hasNext() {
		return currentNumber < addressCount;
	}

	public InetAddress next() {
		currentNumber++;
		random.nextBytes(currentBytes);
		try {
			InetAddressUtils.maskPrototypeAddressBytes(currentBytes, maskBytes, prototypeBytes);
			return InetAddress.getByAddress(currentBytes);
		}
		catch (UnknownHostException e) {
			// this should never happen
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see net.azib.ipscan.feeders.Feeder#getInfo()
	 */
	public String getInfo() {
		try {
			return addressCount + ": " + InetAddress.getByAddress(prototypeBytes).getHostAddress() + " / " + InetAddress.getByAddress(maskBytes).getHostAddress();
		}
		catch (UnknownHostException e) {
			assert false : e;
			return null;
		}
	}
}
