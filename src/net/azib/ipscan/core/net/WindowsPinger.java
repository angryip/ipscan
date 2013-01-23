/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import com.sun.jna.*;
import com.sun.jna.Structure.ByReference;
import com.sun.jna.Structure.ByValue;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.WindowsPinger.IcmpDll.IcmpEchoReply;
import net.azib.ipscan.core.net.WindowsPinger.IcmpDll.IpAddrByVal;

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
		Pointer handle = dll.IcmpCreateFile();
		if (handle == null)  throw new IOException("Unable to create Windows native ICMP handle");

    IpAddrByVal ipaddr = new IpAddrByVal();
    ipaddr.bytes = subject.getAddress().getAddress();

    int sendDataSize = 16;
    int replyDataSize = sendDataSize + (new IcmpEchoReply().size());
    Pointer sendData  = new Memory(sendDataSize);
    Pointer replyData = new Memory(replyDataSize);

    PingResult result = new PingResult(subject.getAddress());
    try {
      for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
        int numReplies = dll.IcmpSendEcho(handle, ipaddr, sendData, (short)sendDataSize, null, replyData, replyDataSize, timeout);
        IcmpEchoReply echoReply = new IcmpEchoReply(replyData);
        if (numReplies > 0 && echoReply.status == 0) {
          result.addReply(echoReply.roundTripTime);
          result.setTTL(echoReply.options.ttl & 0xFF);
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

  public interface IcmpDll extends Library {
    /**
     * Wrapper for Microsoft's {@linkplain http://msdn.microsoft.com/en-US/library/aa366045.aspx IcmpCreateFile}
     */
    public Pointer IcmpCreateFile();

    /**
     * Wrapper for Microsoft's IcmpCreateFile: {@linkplain http://msdn.microsoft.com/en-us/library/aa366043.aspx IcmpCloseHandle}
     */
    public boolean IcmpCloseHandle(Pointer hIcmp);

    /**
     * Wrapper for Microsoft's {@linkplain http://msdn.microsoft.com/EN-US/library/aa366050.aspx IcmpSendEcho}
     */
    public int IcmpSendEcho(
        Pointer     hIcmp,
        IpAddrByVal destinationAddress,
        Pointer     requestData,
        short       requestSize,
        IpOptionInformationByRef requestOptions,
        Pointer     replyBuffer,
        int         replySize,
        int         timeout
    );

    public static class IpAddr extends Structure {
      public byte[] bytes = new byte[4];
    }

    public static class IpAddrByVal extends IpAddr implements ByValue {
    }

    public static class IpOptionInformation extends Structure {
      public byte ttl;
      public byte tos;
      public byte flags;
      public byte optionsSize;
      public Pointer optionsData;
    }

    public static class IpOptionInformationByVal
        extends IpOptionInformation implements ByValue {
    }

    public static class IpOptionInformationByRef
        extends IpOptionInformation implements ByReference {
    }

    public static class IcmpEchoReply extends Structure {
      public IpAddrByVal address;
      public int status;
      public int roundTripTime;
      public short dataSize;
      public short reserved;
      public Pointer data;
      public IpOptionInformationByVal options;

      public IcmpEchoReply() {
      }

      public IcmpEchoReply(Pointer p) {
        useMemory(p);
        read();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    PingResult ping = new WindowsPinger(5000).ping(new ScanningSubject(InetAddress.getLocalHost()), 3);
    System.out.println(ping.getAverageTime() + "ms");
    System.out.println("TTL " + ping.getTTL());
  }
}
