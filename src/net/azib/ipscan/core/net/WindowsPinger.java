/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Windows-only pinger that uses Microsoft's ICMP.DLL for its job.
 * This pinger exists to provide adequate pinging to Windows users,
 * because Microsoft has removed raw socket support from consumer
 * versions of Windows since XP SP2.
 * 
 * @author Anton Keks
 */
public class WindowsPinger implements Pinger {
	
	private int timeout;

	public WindowsPinger(int timeout) {
		this.timeout = timeout; 
	}

	public void close() throws IOException {
	}

	public PingResult ping(InetAddress address, int count) throws IOException {
				
		PingResult result = new PingResult(address);
		byte[] pingData = new byte[56];
		byte[] replyData = new byte[56 + 100];
		
		int handle = nativeIcmpCreateFile();
		try {

			// send a bunch of packets
			for (int i = 1; i <= count; i++) {
				if (nativeIcmpSendEcho(handle, address.getAddress(), pingData, replyData, timeout) > 0) {
					/*if (replyData.status == 11000) {
						result.addReply(replyData.roundTripTime);
						result.setTTL(replyData.ttl);
					}*/
				}
			}
		}
		finally {
			nativeIcmpCloseHandle(handle);
		}
								
		return result;
	}
	
	private native static int nativeIcmpCreateFile();

	private native static int nativeIcmpSendEcho(int handle, byte[] address, byte[] pingData, byte[] replyData, int timeout);
	
	private native static void nativeIcmpCloseHandle(int handle);
}
