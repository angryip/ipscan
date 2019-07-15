/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
 * A feeder, that generates random IP addresses.
 *
 * @author Anton Keks
 */
public class RandomFeeder extends AbstractFeeder {
	
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
	
	public RandomFeeder() {
	}

	public RandomFeeder(String prototypeIP, String mask, int count) {
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

	public ScanningSubject next() {
		currentNumber++;
		random.nextBytes(currentBytes);
		try {
			InetAddressUtils.maskPrototypeAddressBytes(currentBytes, maskBytes, prototypeBytes);
			return new ScanningSubject(InetAddress.getByAddress(currentBytes));
		}
		catch (UnknownHostException e) {
			// this should never happen
			throw new RuntimeException(e);
		}
	}
	
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
