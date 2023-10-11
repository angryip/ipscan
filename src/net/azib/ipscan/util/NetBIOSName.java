package net.azib.ipscan.util;

import java.net.SocketException;

public class NetBIOSName {
	private String computerName;
	private String userName;
	private String groupName;
	private String macAddress;

	private static final int RESPONSE_BASE_LEN = 57;
	private static final int RESPONSE_NAME_LEN = 15;
	private static final int RESPONSE_NAME_BLOCK_LEN = 18;

	private static final int GROUP_NAME_FLAG = 128;
	private static final int NAME_TYPE_DOMAIN = 0x00;
	private static final int NAME_TYPE_MESSENGER = 0x03;

	public NetBIOSName(String computerName, String userName, String groupName, String macAddress) throws SocketException {
		this.computerName = computerName;
		this.userName = userName;
		this.groupName = groupName;
		this.macAddress = macAddress;
	}

	static String[] extractNames(byte[] response, int nameCount) {
		String computerName = nameCount > 0 ? name(response, 0) : null;

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
}
