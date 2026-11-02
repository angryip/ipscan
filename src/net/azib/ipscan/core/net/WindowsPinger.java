/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.WinIpHlpDll.Icmp6EchoReply;
import net.azib.ipscan.core.net.WinIpHlpDll.IcmpEchoReply;
import net.azib.ipscan.core.net.WinIpHlpDll.Ip6SockAddrByRef;

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
	public static final String ID = "pinger.windows";

	private static final int ERROR_INVALID_HANDLE = 6;
	private static final int ERROR_NOT_ENOUGH_MEMORY = 8;
	private static final int ERROR_NOT_SUPPORTED = 50;
	private static final int ERROR_INVALID_PARAMETER = 87;
	private static final int ERROR_INSUFFICIENT_BUFFER = 122;
	private static final int IP_BUF_TOO_SMALL = 11001;
	private static final int IP_NO_RESOURCES = 11006;
	private static final int IP_BAD_OPTION = 11007;
	private static final int IP_HW_ERROR = 11008;
	private static final int IP_BAD_REQ = 11011;
	private static final int IP_GENERAL_FAILURE = 11050;

	private int timeout;

	public WindowsPinger(ScannerConfig config) {
		this.timeout = config.pingTimeout;
	}

	@Override public String getId() {
		return ID;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		if (subject.isIPv6())
			return ping6(subject, count);
		else
			return ping4(subject, count);
	}

	private static boolean isValidHandle(Pointer handle) {
		// IcmpCreateFile returns INVALID_HANDLE_VALUE (-1), which JNA maps to a non-null Pointer
		return handle != null && Pointer.nativeValue(handle) != -1L;
	}

	private static void checkIcmpError(int numReplies) throws IOException {
		if (numReplies != 0) return;

		// a normal timeout is IP_REQ_TIMED_OUT (11010); other errors mean that
		// the ping could not be performed rather than that the host is dead
		var error = Native.getLastError();
		switch (error) {
			case ERROR_INVALID_HANDLE, ERROR_NOT_ENOUGH_MEMORY, ERROR_NOT_SUPPORTED,
				 ERROR_INVALID_PARAMETER, ERROR_INSUFFICIENT_BUFFER, IP_BUF_TOO_SMALL,
				 IP_NO_RESOURCES, IP_BAD_OPTION, IP_HW_ERROR, IP_BAD_REQ, IP_GENERAL_FAILURE ->
				throw new IOException("ICMP request failed with error " + error);
		}
	}

	private PingResult ping4(ScanningSubject subject, int count) throws IOException {
		var handle = dll.IcmpCreateFile();
		if (!isValidHandle(handle)) throw new IOException("Unable to create Windows native ICMP handle");

		var sendDataSize = 32;
		var replyDataSize = sendDataSize + (new IcmpEchoReply().size()) + 10;
		Pointer sendData = new Memory(sendDataSize);
		sendData.clear(sendDataSize);
		Pointer replyData = new Memory(replyDataSize);

		var result = new PingResult(subject.getAddress(), count);
		try {
			var ipaddr = toIpAddr(subject.getAddress());
			for (var i = 1; i <= count && !currentThread().isInterrupted(); i++) {
				var numReplies = dll.IcmpSendEcho(handle, ipaddr, sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
				checkIcmpError(numReplies);
				var echoReply = new IcmpEchoReply(replyData);
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
		var handle = dll.Icmp6CreateFile();
		if (!isValidHandle(handle)) throw new IOException("Unable to create Windows native ICMP6 handle");

		var sendDataSize = 32;
		// Microsoft documents the IPv6 reply buffer as reply + request data + ICMP error + IO_STATUS_BLOCK
		var replyDataSize = sendDataSize + (new Icmp6EchoReply().size()) + 8 + 2 * Native.POINTER_SIZE;
		Pointer sendData = new Memory(sendDataSize);
		sendData.clear(sendDataSize);
		Pointer replyData = new Memory(replyDataSize);

		var result = new PingResult(subject.getAddress(), count);
		try {
			var ipaddr = toIp6Addr(subject.getAddress());
			// JNA structures are not thread-safe, so don't share this instance between scans
			var anyIp6SourceAddr = new Ip6SockAddrByRef();
			for (var i = 1; i <= count && !currentThread().isInterrupted(); i++) {
				var numReplies = dll.Icmp6SendEcho2(handle, null, null, null, anyIp6SourceAddr, toIp6Addr(subject.getAddress()),
						sendData, (short) sendDataSize, null, replyData, replyDataSize, timeout);
				checkIcmpError(numReplies);
				var echoReply = new Icmp6EchoReply(replyData);
				if (numReplies > 0 && echoReply.status == 0 && Arrays.equals(echoReply.address.bytes, ipaddr.bytes)) {
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
