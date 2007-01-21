/**
 * 
 */
package net.azib.ipscan.core;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.core.values.NotAvailableValue;
import net.azib.ipscan.core.values.NotScannedValue;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ScannerTest
 *
 * @author anton
 */
public class ScannerTest {
	
	private Set<Class<? extends Fetcher>> initCalled = new HashSet<Class<? extends Fetcher>>();
	private Set<Class<? extends Fetcher>> cleanupCalled = new HashSet<Class<? extends Fetcher>>();
	private Scanner scanner;
	private FetcherRegistry fetcherRegistry;
	
	@Before
	public void setUp() throws Exception {
		Config.initialize();
		
		fetcherRegistry = createMock(FetcherRegistry.class);
		expect(fetcherRegistry.getSelectedFetchers()).andReturn(
			Arrays.asList(new Fetcher[] {new FakeFetcher(), new AnotherFakeFetcher(), new AbortingFetcher(), new FailingFetcher()})
		);
		replay(fetcherRegistry);
		
		scanner = new Scanner(fetcherRegistry);
	}
	
	@After
	public void tearDown() {
		verify(fetcherRegistry);
	}

	@Test
	public void testScan() throws Exception {
		// scan the local host
		ScanningResult scanningResult = new ScanningResult(InetAddress.getLocalHost(), 4);
		scanner.scan(InetAddress.getLocalHost(), scanningResult);
		
		assertEquals(ScanningSubject.RESULT_TYPE_ALIVE, scanningResult.getType());
		assertEquals(InetAddress.getLocalHost(), scanningResult.getAddress());
		assertEquals(4, scanningResult.getValues().size());
		assertEquals("blah", scanningResult.getValues().get(0));
		assertEquals(NotAvailableValue.INSTANCE, scanningResult.getValues().get(1));
		assertEquals("666 ms", scanningResult.getValues().get(2));
		assertEquals(NotScannedValue.INSTANCE, scanningResult.getValues().get(3));
	}
	
	@Test
	public void testInit() throws Exception {
		scanner.init();
		
		assertTrue(initCalled.contains(FakeFetcher.class));
		assertTrue(initCalled.contains(AnotherFakeFetcher.class));
		assertTrue(initCalled.contains(AbortingFetcher.class));
	}
	
	@Test
	public void testCleanup() throws Exception {
		scanner.cleanup();
		
		assertTrue(cleanupCalled.contains(FakeFetcher.class));
		assertTrue(cleanupCalled.contains(AnotherFakeFetcher.class));
		assertTrue(cleanupCalled.contains(AbortingFetcher.class));
	}

	private class FakeFetcher implements Fetcher {
		public String getLabel() {
			return null;
		}

		public Object scan(ScanningSubject subject) {
			try {
				// check that the IP is correct
				assertEquals(InetAddress.getLocalHost(), subject.getIPAddress());
				
				// set the result type to check after scanning
				subject.setResultType(ScanningSubject.RESULT_TYPE_ALIVE);
				
				// try to set parameter here and read from another Fetcher
				subject.setParameter("megaParam", new Long(211082));
			}
			catch (UnknownHostException e) {
				fail();
			}
			return "blah";
		}

		public void init() {
			initCalled.add(getClass());
		}

		public void cleanup() {
			cleanupCalled.add(getClass());
		}

	}
	
	private class AnotherFakeFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			// the parameter was set by FakeFetcher
			assertEquals(new Long(211082), subject.getParameter("megaParam"));
			// try null as a return value
			return null;
		}
	}
	
	private class AbortingFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			subject.abortScanning();
			return "666 ms";
		}
	}
	
	private class FailingFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			fail("This fetcher should not be reached");
			return null;
		}
	}
	
}
