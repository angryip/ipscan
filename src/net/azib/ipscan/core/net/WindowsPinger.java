/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;
import java.net.InetAddress;

import static java.lang.Thread.*;

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
		int handle = dll.IcmpCreateFile();
		if (handle < 0) {
			throw new IOException("Unable to create Windows native ICMP handle");
		}

    PingResult result = new PingResult(subject.getAddress());
    byte[] pingData = new byte[56];
    IcmpDll.ICMP_ECHO_REPLY replyData = new IcmpDll.ICMP_ECHO_REPLY();
    try {
      for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
        int numReplies = dll.IcmpSendEcho(handle, subject.getAddress().getAddress(), pingData, pingData.length, 0, replyData, replyData.size(), timeout);
        if (numReplies > 0) {
          if (replyData.status == 0) {
						result.addReply(replyData.roundTripTime);
						result.setTTL(replyData.ttl & 0xFF);
					}
				}
			}
		}
		finally {
			dll.IcmpCloseHandle(handle);
		}

		return result;
	}

	public void close() {
		// not needed in this pinger
	}

  public interface IcmpDll extends StdCallLibrary {
    /**
     * Wrapper for Microsoft's  {@linkplain http://msdn.microsoft.com/en-US/library/aa366045.aspx IcmpCreateFile}
     */
    int IcmpCreateFile();

    /**
     * Wrapper for Microsoft's {@linkplain http://msdn.microsoft.com/EN-US/library/aa366050.aspx IcmpSendEcho}
     */
    int IcmpSendEcho(int handle, byte[] address, byte[] pingData, int pingDataSize, int options, ICMP_ECHO_REPLY replyData, int replyDataSize, int timeout);

    /**
     * Wrapper for Microsoft's IcmpCreateFile: {@linkplain http://msdn.microsoft.com/en-us/library/aa366043.aspx IcmpCloseHandle}
     */
    void IcmpCloseHandle(int handle);

    public static class ICMP_ECHO_REPLY extends Structure {
      public byte[] address = new byte[4];
      public int status;
      public int roundTripTime;
      public short dataSize;
      public short reserved;
      public byte ttl;
      public byte tos;
      public byte flags;
      public byte optionsSize;
      public byte[] more = new byte[100];
    }
  }

  public static void main(String[] args) throws IOException {
    PingResult ping = new WindowsPinger(5000).ping(new ScanningSubject(InetAddress.getLocalHost()), 3);
    System.out.println(ping.getAverageTime() + "ms");
    System.out.println("TTL " + ping.getTTL());
  }
}
