/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.azib.ipscan.core.InetAddressUtils;

import org.savarese.vserv.tcpip.OctetConverter;

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
	
	double percentageComplete;
	double percentageIncrement;
	
	/**
	 * @see Feeder#getId()
	 */
	public String getId() {
		return "feeder.range";
	}

	/**
	 * Initializes the RangeFeeder with required parameters
	 * @see Feeder#initialize(String[])
	 * @param params 2 IP addresses:
	 * 		params[0] - startIP
	 * 		params[1] - endIP
	 */
	public int initialize(String ... params) {
		initialize(params[0], params[1]);
		return 2;
	}
		
	public void initialize(String startIP, String endIP) {
		try {
			this.startIP = this.currentIP = InetAddress.getByName(startIP);
			this.endIP = this.originalEndIP = InetAddress.getByName(endIP);
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		if (InetAddressUtils.greaterThan(this.startIP, this.endIP)) {
			throw new FeederException("range.greaterThan");
		}
		initPercentageIncrement();
		this.endIP = InetAddressUtils.increment(this.endIP);
	}
	
	/**
	 * Initalizes fields, used for computation of percentage of completion.
	 */
	private void initPercentageIncrement() {
		// Warning: IPv4 specific code! 
		long rawEndIP = OctetConverter.octetsToInt(this.endIP.getAddress());
		long rawStartIP = OctetConverter.octetsToInt(this.startIP.getAddress());
		// make 32-bit usigned values
		rawEndIP = rawEndIP >= 0 ? rawEndIP : rawEndIP + Integer.MAX_VALUE;
		rawStartIP = rawStartIP >= 0 ? rawStartIP : rawStartIP + Integer.MAX_VALUE;
		// compute 1% of the whole range
		percentageIncrement = 100.0/(rawEndIP - rawStartIP + 1);
		percentageComplete = 0;
	}
	
	public boolean hasNext() {
		// equals() is faster than greaterThan()
		return !currentIP.equals(endIP); 
	}

	public InetAddress next() {
		percentageComplete += percentageIncrement;
		InetAddress prevIP = this.currentIP;
		this.currentIP = InetAddressUtils.increment(prevIP);
		return prevIP;
	}

	public int percentageComplete() {
		return (int)Math.round(percentageComplete);
	}
	
	public String getInfo() {
		// let's return the range
		return startIP.getHostAddress() + " - " + originalEndIP.getHostAddress();
	}
}
