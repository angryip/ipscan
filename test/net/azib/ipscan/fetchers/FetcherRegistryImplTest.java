/**
 * 
 */
package net.azib.ipscan.fetchers;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.prefs.Preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anton Keks
 */
public class FetcherRegistryImplTest {
	
	private Preferences preferences;
	
	private Fetcher ipFetcher;
	private HostnameFetcher hostnameFetcher;
	private CommentFetcher commentFetcher;
	private FetcherRegistry fetcherRegistry;

	@Before
	public void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		preferences.clear();
		
		ipFetcher = new IPFetcher();
		hostnameFetcher = new HostnameFetcher();
		commentFetcher = new CommentFetcher();
		fetcherRegistry = new FetcherRegistryImpl(new Fetcher[] {ipFetcher, hostnameFetcher, commentFetcher}, preferences);
	}
	
	@After
	public void tearDown() throws Exception {
		preferences.removeNode();
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(3, fetcherRegistry.getRegisteredFetchers().size());
		assertEquals(3, fetcherRegistry.getSelectedFetchers().size());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testModifyRegisteredFetchers() throws Exception {
		fetcherRegistry.getRegisteredFetchers().clear();
	}
	
	@Test
	public void testGetSelectedFetcherIndex() throws Exception {
		assertEquals(0, fetcherRegistry.getSelectedFetcherIndex(ipFetcher.getLabel()));
		assertEquals(1, fetcherRegistry.getSelectedFetcherIndex(hostnameFetcher.getLabel()));
		assertEquals(2, fetcherRegistry.getSelectedFetcherIndex(commentFetcher.getLabel()));
		assertEquals(-1, fetcherRegistry.getSelectedFetcherIndex("blah-blah"));
	}
	
	@Test
	public void testLoadPreferences() throws Exception {
		preferences.remove(FetcherRegistryImpl.PREFERENCE_SELECTED_FETCHERS);
		fetcherRegistry = new FetcherRegistryImpl(new Fetcher[] {ipFetcher, hostnameFetcher, commentFetcher}, preferences);
		assertEquals(3, fetcherRegistry.getSelectedFetchers().size());
		
		preferences.put(FetcherRegistryImpl.PREFERENCE_SELECTED_FETCHERS, hostnameFetcher.getLabel() + "###" + commentFetcher.getLabel());
		fetcherRegistry = new FetcherRegistryImpl(new Fetcher[] {ipFetcher, hostnameFetcher, commentFetcher}, preferences);
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertSame(hostnameFetcher, iterator.next());
		assertSame(commentFetcher, iterator.next());
	}
	
	@Test
	public void testUpdateSelectedFetchers() throws Exception {
		// retain only one selected fetcher
		fetcherRegistry.updateSelectedFetchers(new String[] {ipFetcher.getLabel()});
		assertEquals(1, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(ipFetcher.getLabel(), ((Fetcher)iterator.next()).getLabel());
		assertEquals(ipFetcher.getLabel(), preferences.get(FetcherRegistryImpl.PREFERENCE_SELECTED_FETCHERS, null));
		
		// now return a fetcher back
		fetcherRegistry.updateSelectedFetchers(new String[] {commentFetcher.getLabel(), ipFetcher.getLabel()});
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(commentFetcher.getLabel(), ((Fetcher)iterator.next()).getLabel());
		assertEquals(ipFetcher.getLabel(), ((Fetcher)iterator.next()).getLabel());
		assertEquals(commentFetcher.getLabel() + "###" + ipFetcher.getLabel(), preferences.get(FetcherRegistryImpl.PREFERENCE_SELECTED_FETCHERS, null));
	}
	
	@Test
	public void testListener() throws Exception {
		final boolean listenerWasCalled[] = {false};
		fetcherRegistry.addListener(new FetcherRegistryUpdateListener() {
			public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
				assertSame(FetcherRegistryImplTest.this.fetcherRegistry, fetcherRegistry);
				assertEquals(0, fetcherRegistry.getSelectedFetchers().size());
				listenerWasCalled[0] = true;
			}
		});
		fetcherRegistry.updateSelectedFetchers(new String[] {});
		assertTrue(listenerWasCalled[0]);
	}
}
