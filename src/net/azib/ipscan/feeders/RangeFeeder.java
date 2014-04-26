/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.util.InetAddressUtils;
import org.savarese.vserv.tcpip.OctetConverter;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

	public String getId() {
		return "feeder.range";
	}
	
	public RangeFeeder() {
	}
	
	public RangeFeeder(String startIP, String endIP) {
		try {
			this.startIP = this.currentIP = InetAddress.getByName(startIP);
			this.endIP = this.originalEndIP = InetAddress.getByName(endIP);
			this.isReverse = false;
		}
		catch (UnknownHostException e) {
			throw new FeederException("malformedIP");
		}
		if (InetAddressUtils.greaterThan(this.startIP, this.endIP)) {
			this.isReverse = true;
			this.endIP = InetAddressUtils.decrement(InetAddressUtils
					.decrement(this.endIP));
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
		// make 32-bit unsigned values
		rawEndIP = rawEndIP >= 0 ? rawEndIP : rawEndIP + Integer.MAX_VALUE;
		rawStartIP = rawStartIP >= 0 ? rawStartIP : rawStartIP + Integer.MAX_VALUE;

		totalCount = Math.abs(rawEndIP - rawStartIP + 1);
		completedCount = 0;
	}
	
	public boolean hasNext() {
		// equals() is faster than greaterThan()
		return !currentIP.equals(endIP); 
	}

	public ScanningSubject next() {
		completedCount++;
		InetAddress prevIP = this.currentIP;
		if (this.isReverse) {
			this.currentIP = InetAddressUtils.decrement(prevIP);
		} else {
			this.currentIP = InetAddressUtils.increment(prevIP);
		}
		return new ScanningSubject(prevIP);
	}

	@Override
	public String getInfo() {
		return startIP.getHostAddress() + " - " + originalEndIP.getHostAddress();
	}
}
