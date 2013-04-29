/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.WinIpHlpDll.IcmpEchoReply;
import net.azib.ipscan.core.net.WinIpHlpDll.IpAddrByVal;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

import static java.lang.Thread.currentThread;
import static net.azib.ipscan.core.net.WinIpHlpDll.dll;

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

	public WindowsPinger(int timeout) {
		this.timeout = timeout;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		Pointer handle = dll.IcmpCreateFile();
		if (handle == null) throw new IOException("Unable to create Windows native ICMP handle");

		IpAddrByVal ipaddr = new IpAddrByVal();
		ipaddr.bytes = subject.getAddress().getAddress();

		int sendDataSize = 56;
		int replyDataSize = sendDataSize + (new IcmpEchoReply().size()) + 10;
		Pointer sendData = new Memory(sendDataSize);
		sendData.clear(sendDataSize);
		Pointer replyData = new Memory(replyDataSize);

		PingResult result = new PingResult(subject.getAddress());
		try {
			for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
				int numReplies = dll.IcmpSendEcho(handle, ipaddr, sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
				IcmpEchoReply echoReply = new IcmpEchoReply(replyData);
				if (numReplies > 0 && echoReply.status == 0 && Arrays.equals(echoReply.address.bytes, ipaddr.bytes)) {
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

	public static void main(String[] args) throws IOException {
		PingResult ping = new WindowsPinger(5000).ping(new ScanningSubject(InetAddress.getLocalHost()), 3);
		System.out.println(ping.getAverageTime() + "ms");
		System.out.println("TTL " + ping.getTTL());
	}
}
