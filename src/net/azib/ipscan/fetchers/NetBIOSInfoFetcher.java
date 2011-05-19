/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.core.ScanningSubject;

/**
 * NetBIOSInfoFetcher - gathers NetBIOS info about Windows machines.
 * Provided for feature-compatibility with version 2.x
 *
 * @author Anton Keks
 */
public class NetBIOSInfoFetcher extends AbstractFetcher {
	
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private static final int NETBIOS_UDP_PORT = 137;
	private static final byte[] REQUEST_DATA = {(byte)0xA2, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x20, 0x43, 0x4b, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x41, 0x00, 0x00, 0x21, 0x00, 0x01};
	
	private static final int RESPONSE_BASE_LEN = 57;
	private static final int RESPONSE_NAME_LEN = 15;
	private static final int RESPONSE_NAME_BLOCK_LEN = 18;
	
	private static final int GROUP_NAME_FLAG = 128;
	private static final int NAME_TYPE_DOMAIN = 0x00;
	private static final int NAME_TYPE_MESSENGER = 0x03;
	
	private ScannerConfig config;
	
	public NetBIOSInfoFetcher(ScannerConfig config) {
		this.config = config;
	}

	public String getId() {
		return "fetcher.netbios";
	}

	public Object scan(ScanningSubject subject) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(config.pingTimeout);
			
			socket.connect(subject.getAddress(), NETBIOS_UDP_PORT);
			socket.send(new DatagramPacket(REQUEST_DATA, REQUEST_DATA.length));
			
			byte[] response = new byte[1024];
			DatagramPacket responsePacket = new DatagramPacket(response, response.length); 
			socket.receive(responsePacket);
			
			if (responsePacket.getLength() < RESPONSE_BASE_LEN) {
				// response was too short for some reason
				return null;
			}
			
			int nameCount = response[RESPONSE_BASE_LEN-1] & 0xFF;
			if (responsePacket.getLength() < RESPONSE_BASE_LEN + RESPONSE_NAME_BLOCK_LEN * (nameCount-1)) {
				// data was truncated or something is wrong
				return null;
			}
			
			return extractNames(response, nameCount);
		}
		catch (SocketTimeoutException e) {
			// this is not a derivative of SocketException
			return null;
		}
		catch (SocketException e) {
			// this includes PortUnreachableException
			return null;
		}
		catch (Exception e) {
			// bugs?
			LOG.log(Level.WARNING, null, e);
			return null;
		}
		finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	static String extractNames(byte[] response, int nameCount) {
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
		
		return (groupName != null ? groupName + "\\" : "") +
			   (userName != null ? userName + "@" : "") + 
		       computerName + " [" + macAddress + "]";
	}

	private static String name(byte[] response, int i) {
		// as we have no idea in what encoding are the received names,
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
