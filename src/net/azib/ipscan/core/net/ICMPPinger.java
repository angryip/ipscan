/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;
import org.savarese.rocksaw.net.RawSocket;
import org.savarese.vserv.tcpip.ICMPEchoPacket;
import org.savarese.vserv.tcpip.ICMPPacket;
import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.OctetConverter;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.azib.ipscan.util.IOUtils.closeQuietly;

/**
 * Pinging code is encapsulated here.
 * 
 * @author Anton Keks
 */
public class ICMPPinger implements Pinger {
	private static final Logger LOG = LoggerFactory.getLogger();

	private int timeout;
	
	public ICMPPinger(int timeout) {
		this.timeout = timeout;		
	}

	private RawSocket createRawSocket() throws IOException {
		RawSocket socket = new RawSocket();
		socket.open(RawSocket.PF_INET, IPPacket.PROTOCOL_ICMP); 

		try {
			socket.setSendTimeout(timeout);
			socket.setReceiveTimeout(timeout);
		} 
		catch (java.net.SocketException se) {
			socket.setUseSelectTimeout(true);
			socket.setSendTimeout(timeout);
			socket.setReceiveTimeout(timeout);
		}
		return socket;
	}

	private void sendReceiveEchoPacket(RawSocket socket, InetAddress address, int sequence, PingResult result) throws IOException {		
		
		ICMPEchoPacket packet = new ICMPEchoPacket(1);
		byte[] data = new byte[84];
		packet.setData(data);
		packet.setIPHeaderLength(5);
		packet.setICMPDataByteLength(56);
		packet.setType(ICMPPacket.TYPE_ECHO_REQUEST);
		packet.setCode(0);
		packet.setIdentifier(hashCode() & 0xFFFF); // some identification stuff
		packet.setSequenceNumber(sequence);

		int offset = packet.getIPHeaderByteLength();
		int dataOffset = offset + packet.getICMPHeaderByteLength();
		int length = packet.getICMPPacketByteLength();

		OctetConverter.longToOctets(System.currentTimeMillis(), data, dataOffset);
		packet.computeICMPChecksum();

		socket.write(address, data, offset, length);

		try {
			int skippedCount = 0;
			do {
				socket.read(address, data);
				skippedCount++;
				//if (packet.getType() == ICMPPacket.TYPE_ECHO_REPLY)
				//	System.err.println(Thread.currentThread() + " " + packet.getSourceAsInetAddress().getHostAddress() + ": " + skippedCount);
			} 
			while (packet.getType() != ICMPPacket.TYPE_ECHO_REPLY || 
				  packet.getIdentifier() != (hashCode() & 0xFFFF) ||
				  packet.getSequenceNumber() != sequence);

			if (packet.getSourceAsInetAddress().equals(address)) {
				long end = System.currentTimeMillis();
				long start = OctetConverter.octetsToLong(data, dataOffset);
				long time = end - start;
				
				result.addReply(time);
				result.setTTL(packet.getTTL() & 0xFF);
			}
		}
		catch (InterruptedIOException e) {
			// socket read timeout
			LOG.finer("Receive timeout");
			// TODO: make RawSocket to throw Exceptions without the stack trace (for speed)
		}
		catch (UnknownHostException e) {
			LOG.log(Level.WARNING, "Cannot retrieve the source address of an ICMP packet", e);
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Unable to read from the socket", e);
		}

	}

	/**
	 * Issues the specified number of pings and
	 * waits for replies.
	 * 
	 * @param subject address to ping
	 * @param count number of pings to perform
	 */
	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress());
		RawSocket socket = createRawSocket();
		
		try {
			// send a bunch of packets
			for (int i = 0; i < count && !Thread.currentThread().isInterrupted(); i++) {
				try {
					sendReceiveEchoPacket(socket, subject.getAddress(), i, result);
				}
				catch (InterruptedIOException e) {
					// ignore timeouts
				}
			}
		}
		finally {
      closeQuietly(socket);
		}
		
		return result;
	}

	public void close() {
	}
}
