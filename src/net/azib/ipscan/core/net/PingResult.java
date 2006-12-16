/**
 * 
 */
package net.azib.ipscan.core.net;

import java.net.InetAddress;

/**
 * The result of pinging
 *
 * @author anton
 */
public class PingResult {

	InetAddress address;

	int ttl;
	long totalTime;
	int replyCount;
	
	PingResult(InetAddress address) {
		this.address = address;
		
	}

	public int getTTL() {
		return ttl;
	}
	
	public int getAverageTime() {
		return (int)(totalTime / replyCount);
	}
	
	/**
	 * @return true in case at least one reply was received
	 */
	public boolean isAlive() {
		return replyCount > 0;
	}

}
