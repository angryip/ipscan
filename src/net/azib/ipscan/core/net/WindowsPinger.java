/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.WinIpHlpDll.Icmp6EchoReply;
import net.azib.ipscan.core.net.WinIpHlpDll.IcmpEchoReply;
import net.azib.ipscan.core.net.WinIpHlpDll.Ip6SockAddrByRef;
import net.azib.ipscan.core.net.WinIpHlpDll.IpAddrByVal;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.Thread.currentThread;
import static net.azib.ipscan.core.net.WinIpHlp.toIp6Addr;
import static net.azib.ipscan.core.net.WinIpHlp.toIpAddr;
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
	private Ip6SockAddrByRef anyIp6SourceAddr = new Ip6SockAddrByRef();

	public WindowsPinger(ScannerConfig config) {
		this.timeout = config.pingTimeout;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		if (subject.isIPv6())
			return ping6(subject, count);
		else
			return ping4(subject, count);
	}

	private PingResult ping4(ScanningSubject subject, int count) throws IOException {
		Pointer handle = dll.IcmpCreateFile();
		if (handle == null) throw new IOException("Unable to create Windows native ICMP handle");

		int sendDataSize = 32;
		int replyDataSize = sendDataSize + (new IcmpEchoReply().size()) + 10;
		Pointer sendData = new Memory(sendDataSize);
		sendData.clear(sendDataSize);
		Pointer replyData = new Memory(replyDataSize);

		PingResult result = new PingResult(subject.getAddress(), count);
		try {
			IpAddrByVal ipaddr = toIpAddr(subject.getAddress());
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

	private PingResult ping6(ScanningSubject subject, int count) throws IOException {
		Pointer handle = dll.Icmp6CreateFile();
		if (handle == null) throw new IOException("Unable to create Windows native ICMP6 handle");

		int sendDataSize = 32;
		int replyDataSize = sendDataSize + (new Icmp6EchoReply().size()) + 10;
		Pointer sendData = new Memory(sendDataSize);
		sendData.clear(sendDataSize);
		Pointer replyData = new Memory(replyDataSize);

		PingResult result = new PingResult(subject.getAddress(), count);
		try {
			Ip6SockAddrByRef ipaddr = toIp6Addr(subject.getAddress());
			for (int i = 1; i <= count && !currentThread().isInterrupted(); i++) {
				int numReplies = dll.Icmp6SendEcho2(handle, null, null, null, anyIp6SourceAddr, toIp6Addr(subject.getAddress()),
						sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
				Icmp6EchoReply echoReply = new Icmp6EchoReply(replyData);
				if (numReplies > 0 && echoReply.status == 0 && Arrays.equals(echoReply.addressBytes, ipaddr.bytes)) {
					result.addReply(echoReply.roundTripTime);
				}
			}
		}
		finally {
			dll.IcmpCloseHandle(handle);
		}

		return result;
	}
}
