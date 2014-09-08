package net.azib.ipscan.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

public class MDNSResolverTest {
	MDNSResolver resolver;

	@Before
	public void setUp() throws Exception {
		resolver = new MDNSResolver(3000);
	}

	@Test
	public void encodeNameForDNS() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		resolver.writeName(out, "a.bb.ccc.dddd");
		assertEquals("\u0001a\u0002bb\u0003ccc\u0004dddd\u0000", new String(baos.toByteArray()));
	}

	@Test
	public void decodeNameFromDNS() throws Exception {
		byte[] data = "\u0000\u0000\u0001a\u0002bb\u0003ccc\u0004dddd\u0000".getBytes();
		assertEquals("a.bb.ccc.dddd", resolver.decodeName(data, 2, data.length - 2));
	}

	@Test
	public void reverseLookupName() throws Exception {
		assertEquals("2.0.168.192.in-addr.arpa", resolver.reverseName(InetAddress.getByName("192.168.0.2").getAddress()));
	}
}