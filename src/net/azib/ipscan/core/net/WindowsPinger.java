/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.InetAddress;

import net.azib.ipscan.core.LibraryLoader;

/**
 * Windows-only pinger that uses Microsoft's ICMP.DLL for its job.
 * <p/>
 * This pinger exists to provide adequate pinging to Windows users,
 * because Microsoft has removed Raw Socket support from consumer
 * versions of Windows since XP SP2.
 * 
 * @author Anton Keks
 */
public class WindowsPinger implements Pinger {
	
	private int timeout;
	private boolean libraryLoaded;

	public WindowsPinger(int timeout) {
		this.timeout = timeout; 
		if (!libraryLoaded) {
		    LibraryLoader.loadLibrary("winping");
		}
	}

	public PingResult ping(InetAddress address, int count) throws IOException {
				
		PingResult result = new PingResult(address);
		byte[] pingData = new byte[56];
		byte[] replyData = new byte[56 + 100];
		
		int handle = nativeIcmpCreateFile();
		if (handle < 0) {
			throw new IOException("Unable to create Windows native ICMP handle");
		}
			
		try {

			// send a bunch of packets
			for (int i = 1; i <= count; i++) {
				if (nativeIcmpSendEcho(handle, address.getAddress(), pingData, replyData, timeout) > 0) {
					int status = replyData[4] + (replyData[5]<<8) + (replyData[6]<<16) + (replyData[7]<<24);
					if (status == 0) {
						int roundTripTime = replyData[8] + (replyData[9]<<8) + (replyData[10]<<16) + (replyData[11]<<24); 
						int timeToLive = replyData[20] & 0xFF;
						result.addReply(roundTripTime);
						result.setTTL(timeToLive);
					}
				}
			}
		}
		finally {
			nativeIcmpCloseHandle(handle);
		}
								
		return result;
	}
	
	/**
	 * Wrapper for Microsoft's
	 * {@linkplain http://msdn2.microsoft.com/en-US/library/aa366045.aspx IcmpCreateFile}
	 */
	private native static int nativeIcmpCreateFile();

	/**
	 * Wrapper for Microsoft's
	 * {@linkplain http://msdn2.microsoft.com/EN-US/library/aa366050.aspx IcmpSendEcho}
	 */
	private native static int nativeIcmpSendEcho(int handle, byte[] address, byte[] pingData, byte[] replyData, int timeout);
	
	/**
	 * Wrapper for Microsoft's IcmpCreateFile:
	 * {@linkplain http://msdn2.microsoft.com/en-us/library/Aa366043.aspx IcmpCloseHandle}
	 */
	private native static void nativeIcmpCloseHandle(int handle);

	public void close() throws IOException {
		// not needed in this pinger
	}
}
