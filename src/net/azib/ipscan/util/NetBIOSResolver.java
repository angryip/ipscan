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
	private static final int RESPONSE_NAME_LEN = 15;
	private static final int RESPONSE_NAME_BLOCK_LEN = 18;

	private static final int GROUP_NAME_FLAG = 128;
	private static final int NAME_TYPE_DOMAIN = 0x00;
	private static final int NAME_TYPE_MESSENGER = 0x03;

	DatagramSocket socket = new DatagramSocket();

	public NetBIOSResolver(int timeout) throws SocketException {
		socket.setSoTimeout(timeout);
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

		return extractNames(response, nameCount);
	}

	static String[] extractNames(byte[] response, int nameCount) {
		String computerName = name(response, 0);

		String groupName = null;
		for (int i = 1; i < nameCount; i++) {
			if (nameType(response, i) == NAME_TYPE_DOMAIN && (nameFlag(response, i) & GROUP_NAME_FLAG) > 0) {
				groupName = name(response, i);
				break;
			}
		}

		String userName = null;
		for (int i = nameCount - 1; i > 0; i--) {
			if (nameType(response, i) == NAME_TYPE_MESSENGER) {
				userName = name(response, i);
				break;
			}
		}

		String macAddress = String.format("%02X-%02X-%02X-%02X-%02X-%02X",
				nameByte(response, nameCount, 0), nameByte(response, nameCount, 1),
				nameByte(response, nameCount, 2), nameByte(response, nameCount, 3),
				nameByte(response, nameCount, 4), nameByte(response, nameCount, 5));

		return new String[] {computerName, userName, groupName, macAddress};
	}

	private static String name(byte[] response, int i) {
		// as we have no idea in which encoding are the received names,
		// assume that local default encoding matches the remote one (they are on the same LAN most probably)
		return new String(response, RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * i, RESPONSE_NAME_LEN).trim();
	}

	private static int nameByte(byte[] response, int i, int n) {
		return response[RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * i + n] & 0xFF;
	}

	private static int nameFlag(byte[] response, int i) {
		return response[RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * i + RESPONSE_NAME_LEN + 1] & 0xFF +
				(response[RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * i + RESPONSE_NAME_LEN + 2] & 0xFF) * 0xFF;
	}

	private static int nameType(byte[] response, int i) {
		return response[RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * i + RESPONSE_NAME_LEN] & 0xFF;
	}

	public void close() {
		socket.close();
	}
}
