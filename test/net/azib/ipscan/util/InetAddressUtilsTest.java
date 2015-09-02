package net.azib.ipscan.util;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class InetAddressUtilsTest {

	@Test
	public void hostnameMatching() throws Exception {
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("127.0.0.1").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("192.168.245.345").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("a.b").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("angryip.org").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("www.example.com").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("A.B.C").matches());
		assertTrue(InetAddressUtils.HOSTNAME_REGEX.matcher("me.local").matches());

		assertFalse(InetAddressUtils.HOSTNAME_REGEX.matcher("abc").matches());
		assertFalse(InetAddressUtils.HOSTNAME_REGEX.matcher("123").matches());
		assertFalse(InetAddressUtils.HOSTNAME_REGEX.matcher("Hello world.").matches());
	}

	@Test
	public void findIPs() throws Exception {
		Matcher matcher = InetAddressUtils.HOSTNAME_REGEX.matcher("Hello, my IP is 10.10.10.123, not 128.92.34.56. Isn't it cool?");
		assertTrue(matcher.find());
		assertEquals("10.10.10.123", matcher.group());
		assertTrue(matcher.find());
		assertEquals("128.92.34.56", matcher.group());
		assertFalse(matcher.find());
	}

	@Test
	public void findHostnames() throws Exception {
		Matcher matcher = InetAddressUtils.HOSTNAME_REGEX.matcher("Angry IP Scanner's official site it http://angryip.org, not http://www.angryziber.com. Isn't it cool?");
		assertTrue(matcher.find());
		assertEquals("angryip.org", matcher.group());
		assertTrue(matcher.find());
		assertEquals("www.angryziber.com", matcher.group());
		assertFalse(matcher.find());
	}

	@Test
	public void testStartRangeByNetmask() throws UnknownHostException {
		assertEquals("127.0.1.64", InetAddressUtils.startRangeByNetmask(
				InetAddress.getByName("127.0.1.92"), 
				InetAddress.getByName("255.255.255.192")).getHostAddress());
		assertEquals("127.0.0.15", InetAddressUtils.startRangeByNetmask(
				InetAddress.getByName("127.0.0.15"), 
				InetAddress.getByName("255.255.255.255")).getHostAddress());
		assertEquals("192.10.0.0", InetAddressUtils.startRangeByNetmask(
				InetAddress.getByName("192.10.11.13"), 
				InetAddress.getByName("255.255.0.0")).getHostAddress());
	}

	@Test
	public void testEndRangeByNetmask() throws UnknownHostException {
		assertEquals("127.0.1.127", InetAddressUtils.endRangeByNetmask(
				InetAddress.getByName("127.0.1.92"), 
				InetAddress.getByName("255.255.255.192")).getHostAddress());
		assertEquals("127.0.0.15", InetAddressUtils.endRangeByNetmask(
				InetAddress.getByName("127.0.0.15"), 
				InetAddress.getByName("255.255.255.255")).getHostAddress());
		assertEquals("192.10.255.255", InetAddressUtils.endRangeByNetmask(
				InetAddress.getByName("192.10.11.13"), 
				InetAddress.getByName("255.255.0.0")).getHostAddress());
	}
	
	@Test
	public void testIncrement() throws UnknownHostException {
		assertEquals("127.0.0.2", InetAddressUtils.increment(InetAddress.getByName("127.0.0.1")).getHostAddress());
		assertEquals("128.0.0.0", InetAddressUtils.increment(InetAddress.getByName("127.255.255.255")).getHostAddress());
		assertEquals("0.0.0.0", InetAddressUtils.increment(InetAddress.getByName("255.255.255.255")).getHostAddress());
	}

	@Test
	public void testGreaterThan() throws UnknownHostException {
		assertTrue(InetAddressUtils.greaterThan(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("127.0.0.0")));
		assertTrue(InetAddressUtils.greaterThan(InetAddress.getByName("129.0.0.1"), InetAddress.getByName("128.0.0.0")));
		assertTrue(InetAddressUtils.greaterThan(InetAddress.getByName("255.0.0.0"), InetAddress.getByName("254.255.255.255")));
		assertFalse(InetAddressUtils.greaterThan(InetAddress.getByName("0.0.0.0"), InetAddress.getByName("255.255.255.255")));
		assertFalse(InetAddressUtils.greaterThan(InetAddress.getByName("0.0.0.0"), InetAddress.getByName("0.0.0.0")));
		assertFalse(InetAddressUtils.greaterThan(InetAddress.getByName("127.0.0.1"), InetAddress.getByName("127.0.5.0")));
	}	
	
	@Test
	public void testParseNetmask() throws UnknownHostException {
		assertEquals("255.255.255.255", InetAddressUtils.parseNetmask("255.255.255.255").getHostAddress());
		assertEquals("255.255.255.255", InetAddressUtils.parseNetmask("255...255").getHostAddress());
		assertEquals("255.0.255.255", InetAddressUtils.parseNetmask("255.0..255").getHostAddress());
		assertEquals("255.255.255.192", InetAddressUtils.parseNetmask("255...192").getHostAddress());
		assertEquals("255.0.255.0", InetAddressUtils.parseNetmask("255.0..0").getHostAddress());
		assertEquals("0.0.0.0", InetAddressUtils.parseNetmask("0.0.0.0").getHostAddress());

		assertEquals("0.0.0.0", InetAddressUtils.parseNetmask("/0").getHostAddress());
		assertEquals("128.0.0.0", InetAddressUtils.parseNetmask("/1").getHostAddress());
		assertEquals("255.255.0.0", InetAddressUtils.parseNetmask("/16").getHostAddress());
		assertEquals("255.255.255.0", InetAddressUtils.parseNetmask("/24").getHostAddress());
		assertEquals("255.255.255.128", InetAddressUtils.parseNetmask("/25").getHostAddress());
		assertEquals("255.255.255.248", InetAddressUtils.parseNetmask("/29").getHostAddress());
		assertEquals("255.255.255.255", InetAddressUtils.parseNetmask("/32").getHostAddress());
	}
	
	@Test
	public void testMaskPrototypeBytes() throws UnknownHostException {
		byte[] bytes = InetAddress.getByName("32.23.34.254").getAddress();
		InetAddressUtils.maskPrototypeAddressBytes(bytes, InetAddress.getByName("255.0.0.255").getAddress(), InetAddress.getByName("29.1.2.255").getAddress());
		assertEquals("29.23.34.255", InetAddress.getByAddress(bytes).getHostAddress());
		
		bytes = InetAddress.getByName("250.250.250.250").getAddress();
		InetAddressUtils.maskPrototypeAddressBytes(bytes, InetAddress.getByName("0.0.0.0").getAddress(), InetAddress.getByName("29.1.2.255").getAddress());
		assertEquals("250.250.250.250", InetAddress.getByAddress(bytes).getHostAddress());

		bytes = InetAddress.getByName("250.250.250.250").getAddress();
		InetAddressUtils.maskPrototypeAddressBytes(bytes, InetAddress.getByName("255.255.255.255").getAddress(), InetAddress.getByName("29.128.127.73").getAddress());
		assertEquals("29.128.127.73", InetAddress.getByAddress(bytes).getHostAddress());
	}
	
	@Test
	public void testIsLikelyBroadcast() throws UnknownHostException {
		assertTrue(InetAddressUtils.isLikelyBroadcast(InetAddress.getByName("127.0.2.0")));
		assertTrue(InetAddressUtils.isLikelyBroadcast(InetAddress.getByName("127.6.32.255")));
		assertFalse(InetAddressUtils.isLikelyBroadcast(InetAddress.getByName("127.4.5.6")));
	}
}
