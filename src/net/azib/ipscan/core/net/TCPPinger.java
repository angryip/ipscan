/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCP Pinger. Uses a TCP port to ping, doesn't require root privileges.
 *
 * @author anton
 */
public class TCPPinger implements Pinger {
	
	static final Logger LOG = Logger.getLogger(UDPPinger.class.getName());
	
	private static final int PROBE_TCP_PORT = 80;
	
	private int timeout;
	
	public TCPPinger(int timeout) {
		// use double timeout, because TCP connect() produces more packets
		this.timeout = timeout * 2;
	}

	public PingResult ping(InetAddress address, int count) throws IOException {
		PingResult result = new PingResult(address);
		
		Socket socket = new Socket();
		socket.setSoTimeout(timeout);
		
		long startTime = System.currentTimeMillis();
		try {
			socket.connect(new InetSocketAddress(address, PROBE_TCP_PORT), timeout);
			result.addReply(System.currentTimeMillis()-startTime);
		}
		catch (ConnectException e) {
			result.addReply(System.currentTimeMillis()-startTime);
		}
		catch (SocketTimeoutException e) {
		}
		catch (NoRouteToHostException e) {
			// TODO: this means that the host is down
		}
		catch (IOException e) {
			LOG.setLevel(Level.ALL);
			LOG.log(Level.FINER, null, e);
		}

		try {
			socket.close();
		}
		catch (Exception e) {}
		
		return result;
	}

	public void close() throws IOException {
		// nothing to do here
	}
}
