/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * ScannerTest
 *
 * @author anton
 */
public class ScannerTest extends TestCase {
	
	protected void setUp() throws Exception {
		Config.initialize();
	}

	public void test() throws Exception {
		// initialize with fake fetchers
		Scanner scanner = new Scanner(new FakeFetcherRegistry());
		
		// scan the local host
		ScanningResult scanningResult = new ScanningResult(InetAddress.getLocalHost(), 4);
		scanner.scan(InetAddress.getLocalHost(), scanningResult);
		
		assertEquals(ScanningSubject.RESULT_TYPE_ALIVE, scanningResult.getType());
		assertEquals(InetAddress.getLocalHost(), scanningResult.getAddress());
		assertEquals(4, scanningResult.getValues().size());
		assertEquals("blah", scanningResult.getValues().get(0));
		assertEquals(Labels.getLabel("fetcher.value.nothing"), scanningResult.getValues().get(1));
		assertEquals("666 ms", scanningResult.getValues().get(2));
		assertEquals(Labels.getLabel("fetcher.value.aborted"), scanningResult.getValues().get(3));
	}
	
	private class FakeFetcher implements Fetcher {
		public String getLabel() {
			return null;
		}

		public String scan(ScanningSubject subject) {
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
	}
	
	private class AnotherFakeFetcher extends FakeFetcher {
		public String scan(ScanningSubject subject) {
			// the parameter was set by FakeFetcher
			assertEquals(new Long(211082), subject.getParameter("megaParam"));
			// try null as a return value
			return null;
		}
	}
	
	private class AbortingFetcher extends FakeFetcher {
		public String scan(ScanningSubject subject) {
			subject.abortScanning();
			return "666 ms";
		}
	}
	
	private class FailingFetcher extends FakeFetcher {
		public String scan(ScanningSubject subject) {
			fail("This fetcher should not be reached");
			return null;
		}
	}
	
	private class FakeFetcherRegistry implements FetcherRegistry {
		public List getRegisteredFetchers() {
			return null;
		}

		public int getSelectedFetcherIndex(String label) {
			return 0;
		}

		public List getSelectedFetchers() {
			return Arrays.asList(new Fetcher[] {new FakeFetcher(), new AnotherFakeFetcher(), new AbortingFetcher(), new FailingFetcher()});
		}
	}
	
}
