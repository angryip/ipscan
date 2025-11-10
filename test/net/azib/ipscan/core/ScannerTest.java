package net.azib.ipscan.core;

import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.values.NotAvailable;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.AbstractFetcher;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ScannerTest
 *
 * @author Anton Keks
 */
public class ScannerTest {
	
	private Set<Class<? extends Fetcher>> initCalled = new HashSet<>();
	private Set<Class<? extends Fetcher>> cleanupCalled = new HashSet<>();
	private Scanner scanner;
	private FetcherRegistry fetcherRegistry;
	
	@Before
	public void setUp() throws Exception {
		fetcherRegistry = mock(FetcherRegistry.class);
		when(fetcherRegistry.getSelectedFetchers()).thenReturn(
			Arrays.asList(new Fetcher[] {new FakeFetcher(), new AnotherFakeFetcher(), new AddressAbortingFetcher(), new FailingFetcher()})
		);

		scanner = new Scanner(fetcherRegistry);
	}
	
	@Test
	public void testScan() throws Exception {
		// scan the local host
		var scanningResult = new ScanningResult(InetAddress.getLocalHost(), 4);
		scanner.scan(new ScanningSubject(InetAddress.getLocalHost()), scanningResult);
		
		assertEquals(ResultType.ALIVE, scanningResult.getType());
		assertEquals(InetAddress.getLocalHost(), scanningResult.getAddress());
		assertEquals(4, scanningResult.getValues().size());
		assertEquals("blah", scanningResult.getValues().get(0));
		assertEquals(NotAvailable.VALUE, scanningResult.getValues().get(1));
		assertEquals("666 ms", scanningResult.getValues().get(2));
		assertEquals(NotScanned.VALUE, scanningResult.getValues().get(3));
	}
	
	@Test
	public void testScanInterrupted() throws Exception {
		fetcherRegistry = mock(FetcherRegistry.class);
		when(fetcherRegistry.getSelectedFetchers()).thenReturn(
			Arrays.asList(new Fetcher[] {new PlainValueFetcher(), new InterruptedFetcher(), new PlainValueFetcher()})
		);
		scanner = new Scanner(fetcherRegistry);
		
		// scan the local host
		var scanningResult = new ScanningResult(InetAddress.getLocalHost(), 3);
		scanner.scan(new ScanningSubject(InetAddress.getLocalHost()), scanningResult);
		
		assertEquals(ResultType.UNKNOWN, scanningResult.getType());
		assertEquals(InetAddress.getLocalHost(), scanningResult.getAddress());
		assertEquals(3, scanningResult.getValues().size());
		assertEquals("plainValue", scanningResult.getValues().get(0));
		assertEquals(NotScanned.VALUE, scanningResult.getValues().get(1));
		assertEquals(NotScanned.VALUE, scanningResult.getValues().get(2));
		
		// reset interrupted flag
		assertTrue(Thread.interrupted());
	}

	@Test
	public void testInit() {
		scanner.init(mock(Feeder.class));
		
		assertTrue(initCalled.contains(FakeFetcher.class));
		assertTrue(initCalled.contains(AnotherFakeFetcher.class));
		assertTrue(initCalled.contains(AddressAbortingFetcher.class));
	}
	
	@Test
	public void testCleanup() {
		scanner.cleanup();
		
		assertTrue(cleanupCalled.contains(FakeFetcher.class));
		assertTrue(cleanupCalled.contains(AnotherFakeFetcher.class));
		assertTrue(cleanupCalled.contains(AddressAbortingFetcher.class));
	}

	private class FakeFetcher extends AbstractFetcher {
		public String getId() {
			return null;
		}

		public Object scan(ScanningSubject subject) {
			try {
				// check that the IP is correct
				assertEquals(InetAddress.getLocalHost(), subject.getAddress());
				
				// set the result type to check after scanning
				subject.setResultType(ResultType.ALIVE);
				
				// try to set parameter here and read from another Fetcher
				subject.setParameter("megaParam", 211082L);
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
			assertEquals(211082L, subject.getParameter("megaParam"));
			// try null as a return value
			return null;
		}
	}
	
	private class AddressAbortingFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			subject.abortAddressScanning();
			return "666 ms";
		}
	}
	
	private class FailingFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			fail("This fetcher should not be reached");
			return null;
		}
	}
	
	private class PlainValueFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			return "plainValue";
		}
	}
	
	private class InterruptedFetcher extends FakeFetcher {
		public Object scan(ScanningSubject subject) {
			// set the interrupt flag (actually this is set from the outside when user wants to kill the threads)
			Thread.currentThread().interrupt();
			return null;
		}
	}
	
}
