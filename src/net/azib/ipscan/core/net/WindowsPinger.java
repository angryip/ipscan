/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.InetAddress;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import net.azib.ipscan.core.LibraryLoader;
import net.azib.ipscan.core.ScanningSubject;

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
	private IcmpDll dll;

	public WindowsPinger(int timeout) {
		this.timeout = timeout; 
		if (dll == null) {
            try {
		        dll = (IcmpDll) Native.loadLibrary("iphlpapi", IcmpDll.class);
            }
            catch (UnsatisfiedLinkError e) {
                dll = (IcmpDll) Native.loadLibrary("icmp", IcmpDll.class);
            }
		}
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress());
		byte[] pingData = new byte[56];
		byte[] replyData = new byte[56 + 100];
		
		int handle = dll.IcmpCreateFile();
		if (handle < 0) {
			throw new IOException("Unable to create Windows native ICMP handle");
		}
			
		try {
			// send a bunch of packets
			for (int i = 1; i <= count && !Thread.currentThread().isInterrupted(); i++) {
				if (dll.IcmpSendEcho(handle, subject.getAddress().getAddress(), pingData, pingData.length, 0, replyData, replyData.length, timeout) > 0) {
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
			dll.IcmpCloseHandle(handle);
		}
								
		return result;
	}

	public void close() throws IOException {
		// not needed in this pinger
	}

    public interface IcmpDll extends StdCallLibrary {
        /**
         * Wrapper for Microsoft's
         * {@linkplain http://msdn.microsoft.com/en-US/library/aa366045.aspx IcmpCreateFile}
         */
        int IcmpCreateFile();

        /**
         * Wrapper for Microsoft's
         * {@linkplain http://msdn.microsoft.com/EN-US/library/aa366050.aspx IcmpSendEcho}
         */
        int IcmpSendEcho(int handle, byte[] address, byte[] pingData, int pingDataSize, int options, byte[] replyData, int replyDataSize, int timeout);

        /**
         * Wrapper for Microsoft's IcmpCreateFile:
         * {@linkplain http://msdn.microsoft.com/en-us/library/aa366043.aspx IcmpCloseHandle}
         */
        void IcmpCloseHandle(int handle);
    }

    public static void main(String[] args) throws IOException {
        PingResult ping = new WindowsPinger(2000).ping(new ScanningSubject(InetAddress.getLocalHost()), 1);
        System.out.println(ping.getAverageTime());
    }
}
