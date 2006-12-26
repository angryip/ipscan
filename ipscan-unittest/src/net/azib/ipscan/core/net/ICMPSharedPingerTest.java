/**
 * 
 */
package net.azib.ipscan.core.net;

import java.net.InetAddress;

import junit.framework.TestCase;

/**
 * SharedPingerTest
 *
 * @author anton
 */
public class ICMPSharedPingerTest extends TestCase {
	
	public void testPing() throws Exception {
		Pinger pinger = new ICMPSharedPinger(1000);
		PingResult result = pinger.ping(InetAddress.getLocalHost(), 3);
		assertTrue(result.getAverageTime() >= 0);
		assertTrue(result.getAverageTime() < 50);
		assertTrue(result.getTTL() >= 0);
	}

}
