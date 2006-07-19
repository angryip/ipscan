/**
 * 
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;

import org.savarese.rocksaw.net.RawSocket;
import org.savarese.vserv.tcpip.ICMPEchoPacket;
import org.savarese.vserv.tcpip.ICMPPacket;
import org.savarese.vserv.tcpip.OctetConverter;

/**
 * Pinging code is encapsulated here.
 * 
 * @author anton
 */
public class Pinger {

	private RawSocket socket;
	private ICMPEchoPacket packet;
	private InetAddress address;

	private int offset, length, dataOffset;
	private byte[] data;
	
	private long totalTime;
	private int replyCount;

	public Pinger(InetAddress address, int timeout) throws IOException {
		
		this.address = address;
		
		socket = new RawSocket();
		socket.open(RawSocket.PF_INET, 1); // note: protocol is hardcoded, 1 means ICMP

		try {
			socket.setSendTimeout(timeout);
			socket.setReceiveTimeout(timeout);
		} catch (java.net.SocketException se) {
			socket.setUseSelectTimeout(true);
			socket.setSendTimeout(timeout);
			socket.setReceiveTimeout(timeout);
		}
	}

	/**
	 * Closes the raw socket opened by the constructor. After calling this
	 * method, the object cannot be used.
	 */
	public void close() throws IOException {
		socket.close();
	}

	private void sendEchoRequest(int sequence) throws IOException {
		packet = new ICMPEchoPacket(1);
		data = new byte[84];
		packet.setData(data);
		packet.setIPHeaderLength(5);
		packet.setICMPDataByteLength(56);
		packet.setType(ICMPPacket.TYPE_ECHO_REQUEST);
		packet.setCode(0);
		packet.setIdentifier(hashCode() & 0xFFFF); // some identification stuff
		packet.setSequenceNumber(sequence);

		offset = packet.getIPHeaderByteLength();
		dataOffset = offset + packet.getICMPHeaderByteLength();
		length = packet.getICMPPacketByteLength();

		OctetConverter.longToOctets(System.currentTimeMillis(), data, dataOffset);
		packet.computeICMPChecksum();

		socket.write(address, data, offset, length);
	}

	private void receiveEchoReply(int sequence) throws IOException {
		int skippedCount = 0;
		do {
			socket.read(address, data);
			skippedCount++;
		} 
		while (packet.getType() != ICMPPacket.TYPE_ECHO_REPLY || 
			   packet.getIdentifier() != (hashCode() & 0xFFFF) ||
			   packet.getSequenceNumber() != sequence);

		long end = System.currentTimeMillis();
		long start = OctetConverter.octetsToLong(data, dataOffset);
		long time = end - start;
		
		totalTime += time;
		replyCount++;
		
//		System.out.println("recv " + skippedCount);
	}

	/**
	 * Issues the specified number of pings and
	 * waits for replies.
	 * 
	 * @param count number of pings to perform
	 */
	public void ping(int count) throws IOException {
		
		// send a bunch of packets
		for (int i = 0; i < count; i++) {
			try {
				sendEchoRequest(i);
				receiveEchoReply(i);
			}
			catch (InterruptedIOException e) {
				// ignore timeouts
			}
		}
		
		// receive all replies
//		for (int i = 0; i < count; i++) {
//			receiveEchoReply(i);
//		}
	}
	
	public int getTTL() {
		return packet.getTTL() & 0xFF;
	}
	
	public int getAverageTime() {
		return (int)(totalTime / replyCount);
	}

	/**
	 * @return true in case all pings resulted in timeouts
	 */
	public boolean isTimeout() {
		return replyCount == 0;
	}

}
