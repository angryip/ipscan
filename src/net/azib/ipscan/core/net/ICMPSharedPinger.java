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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static java.util.logging.Level.*;
import static net.azib.ipscan.util.IOUtils.*;

/**
 * Shared multi-threaded pinger.
 * 
 * @author Anton Keks
 */
public class ICMPSharedPinger implements Pinger {
	private static final Logger LOG = LoggerFactory.getLogger();

	/** a single raw socket for sending of all ICMP packets */
	private final RawSocket sendingSocket;
	/** a single raw socket for receiving of all ICMP packets */
	private final RawSocket receivingSocket;
	/** the map with PingResults, keys are InetAddress */
	private Map<InetAddress, PingResult> results = new ConcurrentHashMap<InetAddress, PingResult>();
	
	private Thread receiverThread;
	
	private int timeout;
	private int timeOffsetInPacket;

	public ICMPSharedPinger(int timeout) throws IOException {
		// we use two shared sockets, because it works more efficiently
		// OSs tend to copy all received ICMP packets to all open raw sockets,
		// so it is very bad to have a separate raw socket for each scanning thread
		sendingSocket = new RawSocket();
		sendingSocket.open(RawSocket.PF_INET, IPPacket.PROTOCOL_ICMP);
		receivingSocket = new RawSocket();
		receivingSocket.open(RawSocket.PF_INET, IPPacket.PROTOCOL_ICMP);
		this.timeout = timeout; 

		try {
			sendingSocket.setSendTimeout(timeout);
			receivingSocket.setReceiveTimeout(timeout);
			//receivingSocket.setReceiveBufferSize()
		} 
		catch (java.net.SocketException se) {
			sendingSocket.setUseSelectTimeout(true);
			receivingSocket.setUseSelectTimeout(true);
			sendingSocket.setSendTimeout(timeout);
			receivingSocket.setReceiveTimeout(timeout);
		}
		
		receiverThread = new PacketReceiverThread();
		receiverThread.start();
	}

	public void close() throws IOException {
		synchronized (sendingSocket) {
			sendingSocket.close();			
		}
		receiverThread.interrupt();
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		InetAddress address = subject.getAddress();
		PingResult result = new PingResult(address);
		results.put(address, result);
		
		// TODO: make ICMPEchoPacket accept byte array in the constructor
		ICMPEchoPacket packet = new ICMPEchoPacket(1);
		byte[] data = new byte[84];
		packet.setData(data);
		packet.setIPHeaderLength(5);
		packet.setICMPDataByteLength(56);
		packet.setType(ICMPPacket.TYPE_ECHO_REQUEST);
		packet.setCode(0);
		packet.setIdentifier(hashCode() & 0xFFFF); // some identification stuff
		
		try {
			// send a bunch of packets
			// note: we send sequence numbers starting from 1 (this is used by the ReceiverThread)
			for (int i = 1; i <= count  && !Thread.currentThread().isInterrupted(); i++) {
				packet.setSequenceNumber(i);
				
				int offset = packet.getIPHeaderByteLength();
				timeOffsetInPacket = offset + packet.getICMPHeaderByteLength();
				int length = packet.getICMPPacketByteLength();
				
				OctetConverter.longToOctets(System.currentTimeMillis(), data, timeOffsetInPacket);
				packet.computeICMPChecksum();
				
				if (LOG.isLoggable(FINEST)) {
					LOG.finest("Pinging " + i + result.address);
				}
				synchronized (sendingSocket) {
					sendingSocket.write(result.address, data, offset, length);	
				}
				
				try {
					// a small pause between sending the packets
					Thread.sleep(15);
				}
				catch (InterruptedException e) {
					// leave the interrupted flag
					Thread.currentThread().interrupt();
				}
			}
				
			int totalTimeout = timeout * count;
			while (totalTimeout > 0 && result.getReplyCount() < count) {
				if (LOG.isLoggable(FINEST)) {
					LOG.finest("Waiting for response " + address + ": " + totalTimeout);
				}
				synchronized (result) {
					// wait until we have an answer
					try {
						result.wait(timeout);
					}
					catch (InterruptedException ignore) {}
				}
				totalTimeout -= timeout;
			}
			
			return result;
		}
		finally {
			// remove garbage
			results.remove(address);
		}
	}

	/**
	 * An internal thread for receiving of packets
	 */
	private class PacketReceiverThread extends Thread {
		
		public PacketReceiverThread() {
			super("Ping packet receiver");
			setDaemon(true);
			setPriority(Thread.MAX_PRIORITY);
		}

		public void run() {
			ICMPEchoPacket packet = new ICMPEchoPacket(1);
			byte[] data = new byte[84];
			packet.setData(data);
			packet.setIPHeaderLength(5);
			packet.setICMPDataByteLength(56);
			
			// we use this address for receiving
			// due to some reason, raw sockets return packets coming from any addresses anyway
			InetAddress tmpAddress = null;
			try {
				tmpAddress = InetAddress.getLocalHost();
			}
			catch (UnknownHostException e) {
				LOG.log(SEVERE, null, e);
			}
			
			try {
				// Windows OS cannot read from a raw socket before anything has been sent through it
				receivingSocket.write(tmpAddress, data);
			}
			catch (IOException e) {
				LOG.log(WARNING, "Sending of test packet failed", e);
			}
			
			do {
				try {
					receivingSocket.read(tmpAddress, data);
					
					if (packet.getType() == ICMPPacket.TYPE_ECHO_REPLY &&
						packet.getIdentifier() == (ICMPSharedPinger.this.hashCode() & 0xFFFF) &&
						packet.getSequenceNumber() > 0) {
						
						long endTime = System.currentTimeMillis();
						
						PingResult result = results.get(packet.getSourceAsInetAddress());
						if (result == null) {
							LOG.warning("ICMP packet received from an unknown address: " + packet.getSourceAsInetAddress());
							continue;
						}
						
						long startTime = OctetConverter.octetsToLong(data, timeOffsetInPacket);
						long time = endTime - startTime;
						
						if (LOG.isLoggable(FINEST)) {
							LOG.finest("Received " + packet.getSequenceNumber() + packet.getSourceAsInetAddress() + ": " + time);
						}

						result.addReply(time);
						// TTL should be the same among all packets
						result.setTTL(packet.getTTL() & 0xFF);
						
						synchronized (result) {
							// notify the sender that we have an answer :-)
							result.notifyAll();
						}
					}
					else
					if (packet.getType() == ICMPPacket.TYPE_HOST_UNREACHABLE) {
						// TODO: received non-ECHO_REPLY packets may also be useful, saying "destination is unreachable"
						// packet body in this case is the sent ICMP_REQUEST packet
					}
				}
				catch (InterruptedIOException e) {
					// socket read timeout
					LOG.finer("Receive timeout");
					// TODO: make RawSocket to throw Exceptions without the stack trace (for speed)
				}
				catch (UnknownHostException e) {
					LOG.log(WARNING, "Cannot retrieve the source address of an ICMP packet", e);
				}
				catch (IOException e) {
					LOG.log(WARNING, "Unable to read from the socket", e);
				}
			
			}
			while(!interrupted());

      closeQuietly(receivingSocket);
			LOG.fine("Terminated");
		}
	}
}
