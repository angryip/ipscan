/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;

/**
 * ScanningResultListTest
 *
 * @author anton
 */
public class ScanningResultListTest extends TestCase {
	
	ScanningResultList scanningResults = new ScanningResultList();
	
	public void testAdd() throws Exception {
		int index = scanningResults.add(InetAddress.getByName("10.0.0.5"));
		assertEquals("10.0.0.5", scanningResults.getResult(index).getAddress().getHostAddress());
		assertEquals("10.0.0.5", scanningResults.getResult(index).getValues().get(0));
	}
	
	public void testIterator() throws Exception {
		assertFalse(scanningResults.iterator().hasNext());
		scanningResults.add(InetAddress.getLocalHost());
		Iterator i = scanningResults.iterator();
		assertTrue(i.hasNext());
		assertTrue(i.next() instanceof ScanningResult);
		assertFalse(i.hasNext());
	}
	
	public void testClear() throws Exception {
		scanningResults.add(InetAddress.getLocalHost());
		scanningResults.clear();
		assertFalse(scanningResults.iterator().hasNext());
	}
	
	public void testGetResultsAsString() throws Exception {
		Fetcher[] fetchers = new Fetcher[] {new DummyFetcher("fetcher.ip"), new DummyFetcher("fetcher.ping"), new DummyFetcher("fetcher.hostname"), new DummyFetcher("fetcher.ping.ttl")};
		scanningResults.setFetchers(Arrays.asList(fetchers));
		int index = scanningResults.add(InetAddress.getByName("172.28.43.55"));
		ScanningResult result = scanningResults.getResult(index);
		result.getValues().add("123");
		result.getValues().add("xxxxx");
		result.getValues().add(null);
		
		String s = scanningResults.getResultsAsString(index);
		String ln = System.getProperty("line.separator");
		assertTrue(s.endsWith(ln));
		assertTrue(s.indexOf(Labels.getInstance().getString(fetchers[0].getLabel()) + ":\t172.28.43.55" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getInstance().getString(fetchers[1].getLabel()) + ":\t123" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getInstance().getString(fetchers[2].getLabel()) + ":\txxxxx" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getInstance().getString(fetchers[3].getLabel()) + ":\t" + ln) >= 0);
	}
	
	private static class DummyFetcher implements Fetcher {
		
		private String label;
		
		public DummyFetcher(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public String scan(ScanningSubject subject) {
			return null;
		}		
	}
}
