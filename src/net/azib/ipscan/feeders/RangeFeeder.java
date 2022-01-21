/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;
import org.savarese.vserv.tcpip.OctetConverter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static net.azib.ipscan.util.InetAddressUtils.decrement;
import static net.azib.ipscan.util.InetAddressUtils.increment;

/**
 * IP Range Feeder.
 * It contains the starting and ending values, which
 * are then iterated sequentially.
 * 
 * @author Anton Keks
 */
public class RangeFeeder extends AbstractFeeder {
	private InetAddress startIP;
	private InetAddress endIP;
	private InetAddress originalEndIP;
	private InetAddress currentIP;
	boolean isReverse;
	
	double percentageComplete;
	double percentageIncrement;
	
	public String getId() {
		return "feeder.range";
	}
	
	public RangeFeeder() {
	}

	public RangeFeeder(String startIP, String endIP) {
		try {
			this.startIP = this.currentIP = InetAddress.getByName(startIP);
			this.endIP = this.originalEndIP = InetAddress.getByName(endIP);
			initInterfaces(this.startIP);
			this.isReverse = false;
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		if (this.startIP.getClass() != this.endIP.getClass()) {
			throw new FeederException("differentProtocols");
		}
		if (InetAddressUtils.greaterThan(this.startIP, this.endIP)) {
			this.isReverse = true;
			this.endIP = decrement(decrement(this.endIP));
		}
		initPercentageIncrement();
		this.endIP = increment(this.endIP);
	}

	/**
	 * Initalizes fields, used for computation of percentage of completion.
	 */
	private void initPercentageIncrement() {
		byte[] endAddress = this.endIP.getAddress();
		long rawEndIP = OctetConverter.octetsToInt(endAddress, endAddress.length - 4);
		long rawStartIP = OctetConverter.octetsToInt(this.startIP.getAddress(), endAddress.length - 4);
		// make 32-bit unsigned values
		rawEndIP = rawEndIP >= 0 ? rawEndIP : rawEndIP + Integer.MAX_VALUE;
		rawStartIP = rawStartIP >= 0 ? rawStartIP : rawStartIP + Integer.MAX_VALUE;
		// compute 1% of the whole range
		percentageIncrement = Math.abs(100.0 / (rawEndIP - rawStartIP + 1));
		percentageComplete = 0;
	}
	
	@Override public boolean hasNext() {
		// equals() is faster than greaterThan()
		return !currentIP.equals(endIP); 
	}

	@Override public ScanningSubject next() {
		percentageComplete += percentageIncrement;
		InetAddress prevIP = this.currentIP;
		if (this.isReverse) {
			this.currentIP = decrement(prevIP);
		} else {
			this.currentIP = increment(prevIP);
		}
		return subject(prevIP);
	}

	public int percentageComplete() {
		return (int)Math.round(percentageComplete);
	}
	
	@Override public String getInfo() {
		// let's return the range
		return startIP.getHostAddress() + " - " + originalEndIP.getHostAddress();
	}
}
