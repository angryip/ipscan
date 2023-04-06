package net.azib.ipscan.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class NetBIOSResolver implements Closeable {
	private static final int NETBIOS_UDP_PORT = 137;
	private static final byte[] REQUEST_DATA = {(byte)0xA2, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x43, 0x4b, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x00, 0x00, 0x21, 0x00, 0x01};

	private static final int RESPONSE_TYPE_POS = 47;
	private static final byte RESPONSE_TYPE_NBSTAT = 33;
	private static final int RESPONSE_BASE_LEN = 57;
	private static final int RESPONSE_NAME_BLOCK_LEN = 18;

	private DatagramSocket socket;

	public NetBIOSResolver(int timeout) throws SocketException {
		this.socket = new DatagramSocket();
		this.socket.setSoTimeout(timeout);
	}

	public String[] resolve(InetAddress ip) throws IOException {
		socket.send(new DatagramPacket(REQUEST_DATA, REQUEST_DATA.length, ip, NETBIOS_UDP_PORT));

		byte[] response = new byte[1024];
		DatagramPacket responsePacket = new DatagramPacket(response, response.length);
		socket.receive(responsePacket);

		if (responsePacket.getLength() < RESPONSE_BASE_LEN || response[RESPONSE_TYPE_POS] != RESPONSE_TYPE_NBSTAT) {
			return null; // response was too short - no names returned
		}

		int nameCount = response[RESPONSE_BASE_LEN - 1] & 0xFF;
		if (responsePacket.getLength() < RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * nameCount) {
			return null; // data was truncated or something is wrong
		}

		return NetBIOSName.extractNames(response, nameCount);
	}

	public void close() {
		socket.close();
	}
}
