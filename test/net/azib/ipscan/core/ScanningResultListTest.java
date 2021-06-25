package net.azib.ipscan.core;

import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.core.ScanningResultList.StopScanningListener;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ScanningResultListTest
 *
 * @author Anton Keks
 */
public class ScanningResultListTest {
	private List<Fetcher> fetchers = new ArrayList<>(asList(
			mockFetcher("fetcher.ip"), mockFetcher("fetcher.ping"), mockFetcher("fetcher.hostname"), mockFetcher("fetcher.ping.ttl")));

	private FetcherRegistry fetcherRegistry = mock(FetcherRegistry.class);
	private ScanningResultList scanningResults;
	
	@Before
	public void setUp() throws Exception {
		when(fetcherRegistry.getSelectedFetchers()).thenReturn(fetchers);

		scanningResults =  new ScanningResultList(fetcherRegistry);
		scanningResults.initNewScan(mockFeeder("someFeeder"));
	}
	
	@Test
	public void testConstructor() throws Exception {
		StateMachine stateMachine = new StateMachine(){};
		scanningResults = new ScanningResultList(fetcherRegistry, stateMachine);
		scanningResults.initNewScan(mockFeeder("inff"));
		assertFalse(scanningResults.getScanInfo().isCompletedNormally());
		stateMachine.transitionToNext();
		stateMachine.startScanning();
		stateMachine.stop();
		stateMachine.complete();
		assertTrue(scanningResults.getScanInfo().isCompletedNormally());
	}
	
	@Test
	public void testStatisticsInCaseOfNormalFlow() throws Exception {
		// display method: all - first register, then update
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("6.6.6.6"));
		assertFalse(result.isReady());
		assertFalse(scanningResults.isRegistered(result));
		scanningResults.registerAtIndex(0, result);
		
		result.setType(ResultType.WITH_PORTS);
		assertTrue(result.isReady());
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(0, scanningResults.update(result));
		assertEquals(1, scanningResults.getScanInfo().getHostCount());
		assertEquals(1, scanningResults.getScanInfo().getAliveCount());
		assertEquals(1, scanningResults.getScanInfo().getWithPortsCount());
		
		// display method: alive - register only when ready
		result = scanningResults.createResult(InetAddress.getByName("7.7.7.7"));
		result.setType(ResultType.WITH_PORTS);
		assertTrue(result.isReady());
		assertFalse(scanningResults.isRegistered(result));
		scanningResults.registerAtIndex(1, result);
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(2, scanningResults.getScanInfo().getHostCount());
		assertEquals(2, scanningResults.getScanInfo().getAliveCount());
		assertEquals(2, scanningResults.getScanInfo().getWithPortsCount());
		
		// rescan: result is already registered, thus updated twice
		scanningResults.info = new ScanInfo();
		result = scanningResults.createResult(InetAddress.getByName("6.6.6.6"));
		result.setType(ResultType.ALIVE);
		result.reset();
		assertFalse(result.isReady());
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(0, scanningResults.update(result));
		
		result.setType(ResultType.ALIVE);
		assertTrue(result.isReady());
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(0, scanningResults.update(result));
		assertEquals(1, scanningResults.getScanInfo().getHostCount());
		assertEquals(1, scanningResults.getScanInfo().getAliveCount());
		assertEquals(0, scanningResults.getScanInfo().getWithPortsCount());
	}
	
	@Test
	public void testResultType() throws Exception {
		// in some places this kind of greater/lower than comparisions are used
		// so we need to ensure that they are in the correct order
		assertTrue(ResultType.UNKNOWN.ordinal() < ResultType.DEAD.ordinal());
		assertTrue(ResultType.DEAD.ordinal() < ResultType.ALIVE.ordinal());
		assertTrue(ResultType.ALIVE.ordinal() < ResultType.WITH_PORTS.ordinal());
	}
	
	@Test
	public void testCreateResult() throws Exception {
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("10.0.0.5"));
		assertEquals("10.0.0.5", result.getAddress().getHostAddress());
		assertEquals(ResultType.UNKNOWN, result.getType());
		assertEquals(4, result.getValues().size());		
		assertFalse(scanningResults.isRegistered(result));

		result = scanningResults.createResult(InetAddress.getByName("10.0.0.17"));
		assertEquals("10.0.0.17", result.getAddress().getHostAddress());
		assertFalse(scanningResults.isRegistered(result));
		
		assertFalse(scanningResults.iterator().hasNext());
		
		assertEquals(2, scanningResults.getScanInfo().getHostCount());
		assertEquals(0, scanningResults.getScanInfo().getAliveCount());
		assertEquals(0, scanningResults.getScanInfo().getWithPortsCount());
	}
	
	@Test
	public void testRegisterResult() throws Exception {
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("10.0.0.0"));
		result.setType(ResultType.DEAD);
		scanningResults.registerAtIndex(0, result);
		result = scanningResults.createResult(InetAddress.getByName("10.0.0.1"));
		result.setType(ResultType.WITH_PORTS);
		scanningResults.registerAtIndex(1, result);

		result = scanningResults.createResult(InetAddress.getByName("10.0.0.5"));
		result.setType(ResultType.ALIVE);
		scanningResults.registerAtIndex(2, result);
		
		assertTrue(scanningResults.isRegistered(result));
		assertEquals(2, scanningResults.update(result));
		assertSame(result, scanningResults.getResult(2));
		assertSame(result, scanningResults.createResult(InetAddress.getByName("10.0.0.5")));
		
		assertEquals(4, scanningResults.getScanInfo().getHostCount());
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
		assertEquals(1, scanningResults.getScanInfo().getHostCount());
	}
	
	@Test 
	public void testClear() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		scanningResults.clear();
		assertFalse("Results must be empty", scanningResults.iterator().hasNext());
		assertFalse(scanningResults.areResultsAvailable());
		assertFalse(scanningResults.getScanInfo().isCompletedNormally());
	}
	
	@Test
	public void testScanInfoCompletedNormally() throws Exception {
		StopScanningListener stopListener = scanningResults.new StopScanningListener();
		assertFalse(scanningResults.getScanInfo().isCompletedNormally());
		
		stopListener.transitionTo(ScanningState.IDLE, Transition.COMPLETE);
		assertTrue(scanningResults.getScanInfo().isCompletedNormally());

		stopListener.transitionTo(ScanningState.KILLING, Transition.STOP);
		assertFalse(scanningResults.getScanInfo().isCompletedNormally());
	}
	
	@Test 
	public void testInitNewScan() throws Exception {
		fetchers.clear();
		fetchers.add(mockFetcher("hello"));

		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		
		Feeder feeder = mockFeeder("I am the best Feeder in the World!");
		scanningResults.initNewScan(feeder);
		
		assertTrue("initNewScan() must not clear results - otherwise rescanning will be broken", scanningResults.areResultsAvailable());
		assertEquals("Cached Fetchers must be re-initilized", 1, scanningResults.getFetchers().size());
		assertEquals("I am the best Feeder in the World!", scanningResults.getFeederInfo());
		assertEquals("feeder.range", scanningResults.getFeederName());
		assertNotNull(scanningResults.getScanInfo());
		assertFalse("Scanning is not yet finished", scanningResults.getScanInfo().isCompletedNormally());
		assertEquals(0, scanningResults.getScanInfo().getHostCount());
		assertEquals(0, scanningResults.getScanInfo().getAliveCount());
		assertEquals(0, scanningResults.getScanInfo().getWithPortsCount());
	}

	@Test
	public void testCachedFetchers() throws Exception {
		scanningResults.initNewScan(mockFeeder("aaa"));
		fetchers.clear();
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
		assertEquals(0, scanningResults.update(scanningResults.createResult(InetAddress.getByName("127.9.9.1"))));
		assertEquals(1, scanningResults.update(scanningResults.createResult(InetAddress.getByName("127.9.9.4"))));
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
		
		scanningResults.sort(1, true);
		
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
		scanningResults.initNewScan(mockFeeder("abc"));
		List<Fetcher> fetchers = scanningResults.getFetchers();
		ScanningResult result = scanningResults.createResult(InetAddress.getByName("172.28.43.55"));
		scanningResults.registerAtIndex(0, result);
		result.setValue(1, "123");
		result.setValue(2, "xxxxx");
		result.setValue(3, null);
		
		String s = scanningResults.getResult(0).toString();
		String ln = System.getProperty("line.separator");
		assertTrue(s.endsWith(ln));
		assertTrue(s.indexOf(fetchers.get(0).getName() + ":\t172.28.43.55" + ln) >= 0);
		assertTrue(s.indexOf(fetchers.get(1).getName() + ":\t123" + ln) >= 0);
		assertTrue(s.indexOf(fetchers.get(2).getName() + ":\txxxxx" + ln) >= 0);
		assertTrue(s.indexOf(fetchers.get(3).getName() + ":\t" + ln) >= 0);
	}
	
	@Test
	public void testFindText() throws Exception {
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getByName("127.9.9.1")));
		scanningResults.getResult(0).setValue(1, NotScanned.VALUE);
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
	
	@Test
	public void testScanTime() throws Exception {
		ScanInfo scanInfo = scanningResults.getScanInfo();

		assertFalse(scanInfo.isCompletedNormally());
		long scanTime1 = scanInfo.getScanTime();
		assertTrue("Scanning has just begun", scanTime1 >= 0 && scanTime1 <= 10);
		
		Thread.sleep(10);
		scanningResults.new StopScanningListener().transitionTo(ScanningState.IDLE, Transition.COMPLETE);
		assertTrue(scanInfo.isCompletedNormally());
		long scanTime2 = scanInfo.getScanTime();
		assertTrue("Scanning has just finished", scanTime2 >= 10 && scanTime1 <= 20);
		assertTrue(scanTime1 != scanTime2);
		Thread.sleep(10);
		assertEquals(scanTime2, scanInfo.getScanTime());
	}
	
	private Fetcher mockFetcher(String name) {
		Fetcher fetcher = mock(Fetcher.class);
		when(fetcher.getName()).thenReturn(name);
		return fetcher;
	}
	
	private Feeder mockFeeder(String feederInfo) {
		Feeder feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn(feederInfo);
		when(feeder.getName()).thenReturn("feeder.range");
		return feeder;
	}
}
