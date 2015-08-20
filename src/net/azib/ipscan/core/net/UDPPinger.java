/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core.net;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import static java.util.logging.Level.FINER;
import static net.azib.ipscan.util.IOUtils.closeQuietly;

/**
 * UDP Pinger. Uses an UDP port to ping, doesn't require root privileges.
 *
 * @author Anton Keks
 */
public class UDPPinger implements Pinger {
	private static final Logger LOG = LoggerFactory.getLogger();

	private static final int PROBE_UDP_PORT = 37381;

	private int timeout;

	public UDPPinger(int timeout) {
		this.timeout = timeout;
	}

	public PingResult ping(ScanningSubject subject, int count) throws IOException {
		PingResult result = new PingResult(subject.getAddress());

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(timeout);
			socket.connect(subject.getAddress(), PROBE_UDP_PORT);

			for (int i = 0; i < count && !Thread.currentThread().isInterrupted(); i++) {
				byte[] payload = new byte[8];
				long startTime = System.currentTimeMillis();
				ByteBuffer.wrap(payload).putLong(startTime);
				DatagramPacket packet = new DatagramPacket(payload, payload.length);
				try {
					socket.send(packet);
					socket.receive(packet);
				}
				catch (PortUnreachableException e) {
					result.addReply(System.currentTimeMillis() - startTime);
				}
				catch (SocketTimeoutException ignore) {
				}
				catch (NoRouteToHostException e) {
					// this means that the host is down
					break;
				}
				catch (SocketException e) {
					if (e.getMessage().contains(/*No*/"route to host")) {
						// sometimes 'No route to host' also gets here...
						break;
					}
				}
				catch (IOException e) {
					LOG.log(FINER, subject.toString(), e);
				}
			}
			return result;
		}
		finally {
			closeQuietly(socket);
		}
	}

	public void close() {
	}
}
