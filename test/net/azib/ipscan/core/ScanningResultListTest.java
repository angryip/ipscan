/**
 * 
 */
package net.azib.ipscan.core;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.values.NotScannedValue;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ScanningResultListTest
 *
 * @author anton
 */
public class ScanningResultListTest {

	private List<Fetcher> fetchers = new ArrayList<Fetcher>(
			Arrays.asList(new DummyFetcher("fetcher.ip"), new DummyFetcher("fetcher.ping"), new DummyFetcher("fetcher.hostname"), new DummyFetcher("fetcher.ping.ttl")));

	private FetcherRegistry fetcherRegistry;
	private ScanningResultList scanningResults;
	
	@Before
	public void setUp() throws Exception {
		fetcherRegistry = createMock(FetcherRegistry.class);
		expect(fetcherRegistry.getSelectedFetchers())
			.andReturn(fetchers).anyTimes();
		replay(fetcherRegistry);
		
		scanningResults =  new ScanningResultList(fetcherRegistry);
	}
	
	@After
	public void tearDown() {
		verify(fetcherRegistry);
	}
	
	@Test
	public void testCreateResult() throws Exception {
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("10.0.0.5"));
		assertEquals("10.0.0.5", result.getAddress().getHostAddress());
		assertEquals(ScanningSubject.RESULT_TYPE_UNKNOWN, result.getType());
		assertEquals(4, result.getValues().size());		
		assertFalse(scanningResults.isRegistered(result));

		result = scanningResults.createResult(InetAddress.getByName("10.0.0.17"));
		assertEquals("10.0.0.17", result.getAddress().getHostAddress());
		assertFalse(scanningResults.isRegistered(result));
		
		assertFalse(scanningResults.iterator().hasNext());
	}
	
	@Test
	public void testRegisterResult() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getByName("10.0.0.0")));
		scanningResults.registerAtIndex(1, scanningResults.createResult(InetAddress.getByName("10.0.0.1")));

		ScanningResult result = scanningResults.createResult(InetAddress.getByName("10.0.0.5"));		
		scanningResults.registerAtIndex(2, result);
		
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(2, scanningResults.getIndex(result));
		assertSame(result, scanningResults.getResult(2));
		assertSame(result, scanningResults.createResult(InetAddress.getByName("10.0.0.5")));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAlreadyRegistered() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
	}

	@Test
	public void testIterator() throws Exception {
		assertFalse(scanningResults.iterator().hasNext());
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		Iterator<ScanningResult> i = scanningResults.iterator();
		assertTrue(i.hasNext());
		assertEquals(InetAddress.getLocalHost(), i.next().getAddress());
		assertFalse(i.hasNext());
	}
	
	@Test 
	public void testClear() throws Exception {
		fetcherRegistry.getSelectedFetchers().clear();
		fetcherRegistry.getSelectedFetchers().add(new DummyFetcher("hello"));
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		scanningResults.clear();
		
		assertFalse("Results must be empty", scanningResults.iterator().hasNext());
		assertEquals("Cached Fetchers must be re-initilized", 1, scanningResults.getFetchers().size());
	}
	
	@Test
	public void testCachedFetchers() throws Exception {
		fetcherRegistry.getSelectedFetchers().clear();
		assertEquals("Fetchers should be cached from the last scan", 4, scanningResults.getFetchers().size());
	}
	
	@Test
	public void testRemove() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getByName("127.9.9.1")));
		scanningResults.registerAtIndex(1, scanningResults.createResult(InetAddress.getByName("127.9.9.2")));
		scanningResults.registerAtIndex(2, scanningResults.createResult(InetAddress.getByName("127.9.9.3")));
		scanningResults.registerAtIndex(3, scanningResults.createResult(InetAddress.getByName("127.9.9.4")));
		scanningResults.remove(new int[] {1, 2});
		
		Iterator<ScanningResult> i = scanningResults.iterator();
		assertTrue(i.hasNext());
		assertEquals(InetAddress.getByName("127.9.9.1"), i.next().getAddress());
		assertTrue(i.hasNext());
		assertEquals(InetAddress.getByName("127.9.9.4"), i.next().getAddress());
		assertFalse(i.hasNext());
		
		// now check that there are no forgotten indexes
		assertEquals(0, scanningResults.getIndex(scanningResults.createResult(InetAddress.getByName("127.9.9.1"))));
		assertEquals(1, scanningResults.getIndex(scanningResults.createResult(InetAddress.getByName("127.9.9.4"))));
		assertFalse(scanningResults.isRegistered(scanningResults.createResult(InetAddress.getByName("127.9.9.3"))));
		assertFalse(scanningResults.isRegistered(scanningResults.createResult(InetAddress.getByName("127.9.9.2"))));
	}
	
	@Test
	public void testSort() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getByName("127.9.9.1")));
		scanningResults.getResult(0).setValue(1, "x");
		scanningResults.registerAtIndex(1, scanningResults.createResult(InetAddress.getByName("127.9.9.2")));
		scanningResults.getResult(1).setValue(1, "a");
		scanningResults.registerAtIndex(2, scanningResults.createResult(InetAddress.getByName("127.9.9.3")));
		scanningResults.getResult(2).setValue(1, "z");
		scanningResults.registerAtIndex(3, scanningResults.createResult(InetAddress.getByName("127.9.9.4")));
		scanningResults.getResult(3).setValue(1, "m");
		
		scanningResults.sort(1);
		
		Iterator<ScanningResult> i = scanningResults.iterator();
		assertEquals(InetAddress.getByName("127.9.9.2"), i.next().getAddress());
		assertEquals(InetAddress.getByName("127.9.9.4"), i.next().getAddress());
		assertEquals(InetAddress.getByName("127.9.9.1"), i.next().getAddress());
		assertEquals(InetAddress.getByName("127.9.9.3"), i.next().getAddress());
		assertFalse(i.hasNext());
		
		// now check that internal indexes are not broken
		assertEquals(InetAddress.getByName("127.9.9.1"), scanningResults.getResult(2).getAddress());
		assertEquals(InetAddress.getByName("127.9.9.2"), scanningResults.getResult(0).getAddress());
		assertEquals(InetAddress.getByName("127.9.9.4"), scanningResults.getResult(1).getAddress());
	}
	
	@Test 
	public void testGetResultAsString() throws Exception {
		List<Fetcher> fetchers = scanningResults.getFetchers();
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("172.28.43.55"));
		scanningResults.registerAtIndex(0, result);
		result.setValue(1, "123");
		result.setValue(2, "xxxxx");
		result.setValue(3, null);
		
		String s = scanningResults.getResultAsString(0);
		String ln = System.getProperty("line.separator");
		assertTrue(s.endsWith(ln));
		assertTrue(s.indexOf(Labels.getLabel(fetchers.get(0).getLabel()) + ":\t172.28.43.55" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(fetchers.get(1).getLabel()) + ":\t123" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(fetchers.get(2).getLabel()) + ":\txxxxx" + ln) >= 0);
		assertTrue(s.indexOf(Labels.getLabel(fetchers.get(3).getLabel()) + ":\t" + ln) >= 0);
	}
	
	@Test
	public void testFindText() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getByName("127.9.9.1")));
		scanningResults.getResult(0).setValue(1, NotScannedValue.INSTANCE);
		scanningResults.registerAtIndex(1, scanningResults.createResult(InetAddress.getByName("127.9.9.2")));
		scanningResults.getResult(1).setValue(1, new Long(123456789L));
		scanningResults.registerAtIndex(2, scanningResults.createResult(InetAddress.getByName("127.9.9.3")));
		scanningResults.getResult(2).setValue(1, "zzzz");
		scanningResults.registerAtIndex(3, scanningResults.createResult(InetAddress.getByName("127.9.9.4")));
		scanningResults.getResult(3).setValue(1, "mmmmm");
		scanningResults.registerAtIndex(4, scanningResults.createResult(InetAddress.getByName("127.9.9.5")));
		scanningResults.getResult(4).setValue(1, null);
		scanningResults.registerAtIndex(5, scanningResults.createResult(InetAddress.getByName("127.9.9.6")));
		scanningResults.getResult(5).setValue(1, InetAddress.getByName("127.0.0.1"));
		
		assertEquals(-1, scanningResults.findText("sometext", 0));
		assertEquals(1, scanningResults.findText("345", 0));
		assertEquals(-1, scanningResults.findText("345", 2));
		assertEquals(3, scanningResults.findText("m", 2));
		assertEquals(5, scanningResults.findText("0.0.", 2));
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

		public void cleanup() {
		}

		public void init() {
		}		
	}
	
}
