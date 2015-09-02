package net.azib.ipscan.feeders;

import net.azib.ipscan.config.LabelsTest;
import net.azib.ipscan.core.ScanningSubject;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.util.Iterator;

import static net.azib.ipscan.feeders.FeederTestUtils.assertFeederException;
import static org.junit.Assert.*;

/**
 * Test of FileFeeder
 *
 * @author Anton Keks
 */
public class FileFeederTest {

	@Test
	public void simpleIPs() throws FeederException {
		StringReader reader = new StringReader("10.11.12.13 10.11.12.14 10.11.12.15");
		FileFeeder fileFeeder = new FileFeeder(reader);
		assertTrue(fileFeeder.hasNext());
		assertEquals("10.11.12.13", fileFeeder.next().getAddress().getHostAddress());
		assertTrue(fileFeeder.hasNext());
		assertEquals("10.11.12.14", fileFeeder.next().getAddress().getHostAddress());
		assertTrue(fileFeeder.hasNext());
		assertEquals("10.11.12.15", fileFeeder.next().getAddress().getHostAddress());
		assertFalse(fileFeeder.hasNext());
	}

	@Test
	public void simpleHostnames() throws FeederException {
		StringReader reader = new StringReader("angryip.org, www.google.ee");
		FileFeeder fileFeeder = new FileFeeder(reader);
		assertTrue(fileFeeder.hasNext());
		assertEquals("angryip.org", fileFeeder.next().getAddress().getHostName());
		assertTrue(fileFeeder.hasNext());
		assertEquals("www.google.ee", fileFeeder.next().getAddress().getHostName());
		assertFalse(fileFeeder.hasNext());
	}

	@Test
	public void testStringParams() {
		try {
			new FileFeeder(new File(LabelsTest.findBaseDir(), "Makefile").getPath());
			fail();
		}
		catch (FeederException e) {
			assertEquals("file.nothingFound", e.getMessage());
		}
	}
	
	@Test
	public void testNoFile() {
		try {
			new FileFeeder("no_such_file.txt");
			fail();
		}
		catch (FeederException e) {
			assertFeederException("file.notExists", e);
		}		
	}
	
	@Test
	public void testNothingFound() {
		try {
			StringReader reader = new StringReader("no ip addresses here");			
			new FileFeeder(reader);
			fail();
		}
		catch (FeederException e) {
			FeederTestUtils.assertFeederException("file.nothingFound", e);
		}
	}
	
	@Test
	public void extractFromDifferentFormats() {
		assertAddressCount("The 127.0.0.1 is the localhost IP,\n but 192.168.255.255 is probably a broadcast IP", 2);
		
		assertAddressCount("1.1.1.,1245\n2.2.2.2:123\n3.3.3.3.3.3\n\n\n9.9.9.9999", 2);
		
		assertAddressCount("1.2.3.4", 1);
		
		assertAddressCount("1.2.3.4:125\n2.3.4.255:347", 2);
		
		assertAddressCount("255.255.255.255\n\n\n\t0.0.0.0", 2);

		// This test fails under GCJ, probably it doesn't normalize IP addresses,
		// passed to the InetAddress and throws UnknownHostException because of the leading zero
		// assertAddressCount("09.001.005.006", 1);

		assertAddressCount("999.999.999.999,1.1.01.1", 1);

		assertAddressCount("<xml>66.87.99.128</xml>\n<xml>000.87.99.129</xml>0000.1.1.1", 3);
	}
			
	@Test
	public void testGetPercentageComplete() throws Exception {
		StringReader reader = new StringReader("1.2.3.4, 2.3.4.5, mega cool 0.0.0.0");
		FileFeeder fileFeeder = new FileFeeder(reader);
		assertEquals(0, fileFeeder.percentageComplete());
		fileFeeder.next();
		assertEquals(33, fileFeeder.percentageComplete());
		fileFeeder.next();
		assertEquals(67, fileFeeder.percentageComplete());
		fileFeeder.next();
		assertEquals(100, fileFeeder.percentageComplete());
		
		reader = new StringReader("255.255.255.255");
		fileFeeder = new FileFeeder(reader);
		assertEquals(0, fileFeeder.percentageComplete());
		fileFeeder.next();
		assertEquals(100, fileFeeder.percentageComplete());
	}	
	
	@Test
	public void testGetInfo() {
		StringReader reader = new StringReader("255.255.255.255, 2.3.4.5, mega cool 0.0.0.0");
		FileFeeder fileFeeder = new FileFeeder(reader);
		assertEquals("3", fileFeeder.getInfo());
	}
	
	@Test
	public void requestedPortsAreDetected() throws Exception {
		StringReader reader = new StringReader("1.2.3.4:1234\n2.3.4.5:\n 7.6.5.4:789004\n 1.2.3.5:80  1.2.3.5:3128 ");
		FileFeeder fileFeeder = new FileFeeder(reader);
		
		assertEquals(1234, (int)fileFeeder.next().requestedPortsIterator().next());
		assertFalse(fileFeeder.next().isAnyPortRequested());
		assertFalse(fileFeeder.next().isAnyPortRequested());
		
		ScanningSubject lastSubject = fileFeeder.next();
		assertEquals("1.2.3.5", lastSubject.getAddress().getHostAddress());
		Iterator<Integer> portIterator = lastSubject.requestedPortsIterator();
		assertEquals(80, (int)portIterator.next());
		assertEquals(3128, (int)portIterator.next());
	}

	private void assertAddressCount(String s, int addressCount) {
		StringReader reader = new StringReader(s);			
		FileFeeder feeder = new FileFeeder(reader);
		int numAddresses = 0;
		while (feeder.hasNext()) {
			feeder.next();
			numAddresses++;
		}
		assertEquals(addressCount, numAddresses);
	}

}
