/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.core.ScanningSubject;

/**
 * TCP Pinger. Uses a TCP port to ping, doesn't require root privileges.
 *
 * @author Anton Keks
 */
public class TCPPinger implements Pinger {
	
	static final Logger LOG = Logger.getLogger(TCPPinger.class.getName());
	
	// try different ports in sequence, starting with 80 (which is most probably not filtered)
	private static final int[] PROBE_TCP_PORTS = {80, 80, 443, 8080, 22, 7};
	
	private int timeout;
	
	public TCPPinger(int timeout) {
		// use increased timeout, because TCP connect() produces more packets (roundtrips)
		this.timeout = timeout + timeout/2;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress());
		int workingPort = -1;
		
		for (int i = 0; i < count && !Thread.currentThread().isInterrupted(); i++) {
			Socket socket = new Socket();
			long startTime = System.currentTimeMillis();
			try {
				// cycle through different ports until a working one is found
				int probePort = workingPort >= 0 ? workingPort : PROBE_TCP_PORTS[i % PROBE_TCP_PORTS.length];
				// change the first port to the requested one, if it is available
				if (i == 0 && subject.isAnyPortRequested()) {
					probePort = subject.requestedPortsIterator().next();
				}
				
				// set some optimization options
				socket.setReuseAddress(true);
				socket.setReceiveBufferSize(32);
				socket.connect(new InetSocketAddress(subject.getAddress(), probePort), timeout);				
				if (socket.isConnected()) {
					// it worked - success
					success(result, startTime);
					// it worked! - remember the current port
					workingPort = probePort;
				}
			}
			catch (SocketTimeoutException e) {
			}
			catch (NoRouteToHostException e) {
				// this means that the host is down
				break;
			}
			catch (IOException e) {
				String msg = e.getMessage();
				
				// RST should result in ConnectException, but not all Java implementations respect that
				if (msg.contains(/*Connection*/"refused")) {
					// we've got an RST packet from the host - it is alive
					success(result, startTime);
				}
				else
				// this should result in NoRouteToHostException or ConnectException, but not all Java implementation respect that
				if (msg.contains(/*No*/"route to host") || msg.contains(/*Host is*/"down")) {
					// host is down
					break;
				}
				else {
					// something unknown
					LOG.log(Level.FINER, subject.toString(), e);
				}
			}
			finally {
				try {
					socket.close();
				}
				catch (Exception e) {}
			}
		}
		
		return result;
	}
	
	private void success(PingResult result, long startTime) {
		result.addReply(System.currentTimeMillis()-startTime);
		// one positive result is enough for TCP 
		result.enableTimeoutAdaptation();		
	}

	public void close() throws IOException {
		// nothing to do here
	}
}
