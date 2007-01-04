/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UDP Pinger. Uses an UDP port to ping, doesn't require root privileges.
 *
 * @author anton
 */
public class UDPPinger implements Pinger {
	
	private static final Logger LOG = Logger.getLogger(UDPPinger.class.getName());
	
	private static final int PROBE_UDP_PORT = 33381;
	
	private int timeout;
	
	public UDPPinger(int timeout) {
		this.timeout = timeout;
	}

	public PingResult ping(InetAddress address, int count) throws IOException {
		PingResult result = new PingResult(address);
		
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(timeout);
		socket.connect(address, PROBE_UDP_PORT);
		
		for (int i = 0; i < count; i++) {
			DatagramPacket packet = new DatagramPacket(new byte[]{}, 0);
			long startTime = System.currentTimeMillis();
			try {
				socket.send(packet);
				socket.receive(packet);
			}
			catch (PortUnreachableException e) {
				result.addReply(System.currentTimeMillis()-startTime);
			}
			catch (SocketTimeoutException e) {
			}
			catch (IOException e) {
				LOG.log(Level.FINER, null, e);
			}
		}
		
		socket.disconnect();
		
		return result;
	}

	public void close() throws IOException {
		// nothing to do here
	}
}
