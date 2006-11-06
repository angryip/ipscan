/**
 * 
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.azib.ipscan.config.Labels;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

/**
 * ScanningResultListTest
 *
 * @author anton
 */
public class ScanningResultListTest extends TestCase {
	
	private FetcherRegistry fetcherRegistry;
	private ScanningResultList scanningResults;
	
	protected void setUp() throws Exception {
		fetcherRegistry = new DummyFetcherRegistry();
		scanningResults =  new ScanningResultList(fetcherRegistry);
	}

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
		fetcherRegistry.getSelectedFetchers().clear();
		fetcherRegistry.getSelectedFetchers().add(new DummyFetcher("hello"));
		scanningResults.add(InetAddress.getLocalHost());
		scanningResults.clear();
		
		assertFalse("Results must be empty", scanningResults.iterator().hasNext());
		assertEquals("Cached Fetchers must be re-initilized", 1, scanningResults.getFetchers().size());
	}
	
	public void testCachedFetchers() throws Exception {
		fetcherRegistry.getSelectedFetchers().clear();
		assertEquals("Fetchers should be cached from the last scan", 4, scanningResults.getFetchers().size());
	}
	
	public void testRemove() throws Exception {
		scanningResults.add(InetAddress.getByName("127.9.9.1"));
		int i2 = scanningResults.add(InetAddress.getByName("127.9.9.2"));
		int i3 = scanningResults.add(InetAddress.getByName("127.9.9.3"));
		scanningResults.add(InetAddress.getByName("127.9.9.4"));
		scanningResults.remove(new int[] {i2,i3});
		
		Iterator i = scanningResults.iterator();
		assertTrue(i.hasNext());
		assertEquals(InetAddress.getByName("127.9.9.1"), ((ScanningResult)i.next()).getAddress());
		assertTrue(i.hasNext());
		assertEquals(InetAddress.getByName("127.9.9.4"), ((ScanningResult)i.next()).getAddress());
		assertFalse(i.hasNext());
	}
	
	public void testSort() throws Exception {
		scanningResults.add(InetAddress.getByName("127.9.9.1"));
		scanningResults.getResult(0).setValue(1, "x");
		scanningResults.add(InetAddress.getByName("127.9.9.2"));
		scanningResults.getResult(1).setValue(1, "a");
		scanningResults.add(InetAddress.getByName("127.9.9.3"));
		scanningResults.getResult(2).setValue(1, "z");
		scanningResults.add(InetAddress.getByName("127.9.9.4"));
		scanningResults.getResult(3).setValue(1, "m");
		
		scanningResults.sort(1);
		
		Iterator i = scanningResults.iterator();
		assertEquals(InetAddress.getByName("127.9.9.2"), ((ScanningResult)i.next()).getAddress());
		assertEquals(InetAddress.getByName("127.9.9.4"), ((ScanningResult)i.next()).getAddress());
		assertEquals(InetAddress.getByName("127.9.9.1"), ((ScanningResult)i.next()).getAddress());
		assertEquals(InetAddress.getByName("127.9.9.3"), ((ScanningResult)i.next()).getAddress());
		assertFalse(i.hasNext());
	}
	
	public void testGetResultsAsString() throws Exception {
		List fetchers = scanningResults.getFetchers();
		int index = scanningResults.add(InetAddress.getByName("172.28.43.55"));
		ScanningResult result = scanningResults.getResult(index);
		result.setValue(1, "123");
		result.setValue(2, "xxxxx");
		result.setValue(3, null);
		
		String s = scanningResults.getResultsAsString(index);
		String ln = System.getProperty("line.separator");
		assertTrue(s.endsWith(ln));
		assertTrue(s.indexOf(Labels.getLabel(((Fetcher)fetchers.get(0)).getLabel()) + ":\t172.28.43.55" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(((Fetcher)fetchers.get(1)).getLabel()) + ":\t123" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(((Fetcher)fetchers.get(2)).getLabel()) + ":\txxxxx" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(((Fetcher)fetchers.get(3)).getLabel()) + ":\t" + ln) >= 0);
	}
	
	private static class DummyFetcher implements Fetcher {
		private String label;
		
		public DummyFetcher(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public Object scan(ScanningSubject subject) {
			return null;
		}		
	}
	
	private static class DummyFetcherRegistry implements FetcherRegistry {
		
		private List fetchers = new ArrayList(Arrays.asList(new Fetcher[] {new DummyFetcher("fetcher.ip"), new DummyFetcher("fetcher.ping"), new DummyFetcher("fetcher.hostname"), new DummyFetcher("fetcher.ping.ttl")}));
		
		public List getRegisteredFetchers() {
			return null;
		}

		public int getSelectedFetcherIndex(String label) {
			return 0;
		}
		
		public List getSelectedFetchers() {
			return fetchers;
		}
	}
}
