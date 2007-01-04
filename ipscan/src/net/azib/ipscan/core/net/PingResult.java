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

	private int ttl;
	private long totalTime;
	private long longestTime;
	private int replyCount;
	
	public PingResult(InetAddress address) {
		this.address = address;		
	}
	
	public void addReply(long time) {
		replyCount++;
		if (time > longestTime)
			longestTime = time;
		totalTime += time;
	}

	public int getTTL() {
		return ttl;
	}
	
	public void setTTL(int ttl) {
		this.ttl = ttl;
	}
	
	public int getAverageTime() {
		return (int)(totalTime / replyCount);
	}
	
	public int getLongestTime() {
		return (int)longestTime;
	}
	
	public int getReplyCount() {
		return replyCount;
	}
	
	/**
	 * @return true in case at least one reply was received
	 */
	public boolean isAlive() {
		return replyCount > 0;
	}

}
