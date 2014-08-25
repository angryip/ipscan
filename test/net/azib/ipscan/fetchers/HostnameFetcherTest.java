/**
 * 
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * HostnameFetcherTest
 *
 * @author Anton Keks
 */
public class HostnameFetcherTest extends AbstractFetcherTestCase {

	@Before
	public void setUp() throws Exception {
		fetcher = new HostnameFetcher();
	}
	
	@Test
	public void testScan() throws UnknownHostException {
		// Some of these tests are run inside of if's to prevent their failing on certain network configurations
		if (!InetAddress.getLocalHost().getCanonicalHostName().equals(InetAddress.getLocalHost().getHostAddress()))
			assertEquals(InetAddress.getLocalHost().getCanonicalHostName(), fetcher.scan(new ScanningSubject(InetAddress.getLocalHost())));
		
		try {
			InetAddress address = InetAddress.getByName("angryip.org");
			assertEquals("pages.github.com", fetcher.scan(new ScanningSubject(address)));
		}
		catch (UnknownHostException e) { /* ignore - test is running in off-line environment */ }
		
		InetAddress inexistentAddress = InetAddress.getByName("192.168.253.253");
		if (inexistentAddress.getHostName().equals("192.168.253.253"))
			assertNull(fetcher.scan(new ScanningSubject(inexistentAddress)));			
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

	@Test
	public void ownHostname() throws Exception {
		DatagramSocket mdns = new DatagramSocket();
		mdns.setSoTimeout(3000);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		out.write(new byte[] {0x17, 0x57, 1, 0x20, 0, 1, 0, 0, 0, 0, 0, 1});
		writeName(out, "2.0.168.192.in-addr.arpa");
//		out.write("\u00012\u00010\u0003168\u0003192\u0007in-addr\u0004arpa".getBytes());
		out.write(new byte[] {0, 0xc, 0, 1});
		out.write(new byte[] {0, 0, 0x29, 0x10, 0, 0, 0, 0, 0, 0, 0});
		DatagramPacket query = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, InetAddress.getByName("224.0.0.251"), 5353);
		mdns.send(query);

		DatagramPacket resp = new DatagramPacket(new byte[512], 512);
		mdns.receive(resp);
		byte[] data = resp.getData();
		int offset = query.getLength() - 11 + 12 + 1;
		System.out.println(new String(data, offset, resp.getLength() - offset - 1));
	}
}
