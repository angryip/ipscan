package net.azib.ipscan.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MDNSResolver {
	DatagramSocket mdns = new DatagramSocket();
	InetAddress mdnsIP = InetAddress.getByName("224.0.0.251");
	private int mdnsPort = 5353;

	public MDNSResolver() throws IOException {
		mdns.setSoTimeout(3000);
	}

	void writeName(DataOutputStream out, String name) throws IOException {
		int s = 0, e = 0;
		while ((e = name.indexOf('.', s)) != -1) {
			out.writeByte(e - s);
			out.write(name.substring(s, e).getBytes());
			s = e + 1;
		}
		out.write(name.length() - s);
		out.write(name.substring(s).getBytes());
		out.writeByte(0);
	}

	String decodeName(byte[] data, int offset, int length) {
		StringBuilder s = new StringBuilder(length);
		for (int i = offset; i < offset + length; i++) {
			byte len = data[i];
			s.append(new String(data, i + 1, len)).append('.');
			i += len;
		}
		s.setLength(s.length() - 2);
		return s.toString();
	}

	public byte[] dnsRequest(String name) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.write(new byte[] {0x17, 0x57, 1, 0x20, 0, 1, 0, 0, 0, 0, 0, 1});
		writeName(out, name);
		out.write(new byte[] {0, 0xc, 0, 1});
		out.write(new byte[]{0, 0, 0x29, 0x10, 0, 0, 0, 0, 0, 0, 0});
		return baos.toByteArray();
	}

	public String resolve(InetAddress ip) throws IOException {
		byte[] addr = ip.getAddress();
		byte[] data = dnsRequest((addr[3]&0xFF) + "." + (addr[2]&0xFF) + "." + (addr[1]&0xFF) + "." + (addr[0]&0xFF) + ".in-addr.arpa");

		DatagramPacket query = new DatagramPacket(data, data.length, mdnsIP, mdnsPort);
		mdns.send(query);

		DatagramPacket resp = new DatagramPacket(new byte[512], 512);
		mdns.receive(resp);
		data = resp.getData();
		int offset = query.getLength() - 11 + 12;
		return decodeName(data, offset, resp.getLength() - offset);
	}

	public static void main(String[] args) throws IOException {
		System.out.println(new MDNSResolver().resolve(InetAddress.getByName("192.168.0.2")));
	}
}
