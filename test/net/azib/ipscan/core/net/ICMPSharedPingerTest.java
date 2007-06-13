/**
 * 
 */
package net.azib.ipscan.core.net;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Ignore;
import org.junit.Test;

/**
 * SharedPingerTest
 *
 * @author anton
 */
public class ICMPSharedPingerTest {
	
	@Test @Ignore("this test works only under root")
	public void testPing() throws Exception {
		Pinger pinger = new ICMPSharedPinger(1000);
		PingResult result = pinger.ping(InetAddress.getLocalHost(), 3);
		assertTrue(result.getAverageTime() >= 0);
		assertTrue(result.getAverageTime() < 50);
		assertTrue(result.getTTL() >= 0);
	}

}
