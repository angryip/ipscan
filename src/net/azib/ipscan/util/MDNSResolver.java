package net.azib.ipscan.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MDNSResolver implements Closeable {
	DatagramSocket mdns = new DatagramSocket();
	InetAddress mdnsIP = InetAddress.getByName("224.0.0.251");
	private int mdnsPort = 5353;

	public MDNSResolver(int pingTimeout) throws IOException {
		mdns.setSoTimeout(pingTimeout);
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

	byte[] dnsRequest(int id, String name) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.writeShort(id);
		out.write(new byte[] { 1, 0x20, 0, 1, 0, 0, 0, 0, 0, 1});
		writeName(out, name);
		out.write(new byte[] {0, 0xc, 0, 1});
		out.write(new byte[]{0, 0, 0x29, 0x10, 0, 0, 0, 0, 0, 0, 0});
		return baos.toByteArray();
	}

	String reverseName(byte[] addr) {
		// note: only IPv4 is supported here
		return (addr[3]&0xFF) + "." + (addr[2]&0xFF) + "." + (addr[1]&0xFF) + "." + (addr[0]&0xFF) + ".in-addr.arpa";
	}

	public String resolve(InetAddress ip) throws IOException {
		byte[] addr = ip.getAddress();
		int requestId = addr[2]*0xFF + addr[3];
		byte[] request = dnsRequest(requestId, reverseName(addr));
		mdns.send(new DatagramPacket(request, request.length, mdnsIP, mdnsPort));

		DatagramPacket respPacket = new DatagramPacket(new byte[512], 512);
		mdns.receive(respPacket);
		byte[] response = respPacket.getData();
		if (response[0] != request[0] && response[1] != request[1]) return null;
		int offset = request.length + 1;
		return decodeName(response, offset, respPacket.getLength() - offset);
	}

	public void close() {
		mdns.close();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(new MDNSResolver(3000).resolve(InetAddress.getByName("192.168.0.2")));
	}
}
