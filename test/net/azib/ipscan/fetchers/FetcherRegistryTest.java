/**
 * 
 */
package net.azib.ipscan.fetchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.prefs.Preferences;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author Anton Keks
 */
public class FetcherRegistryTest {
	
	private Preferences preferences;
	
	private Fetcher ipFetcher;
	private PingFetcher pingFetcher;
	private HostnameFetcher hostnameFetcher;
	private CommentFetcher commentFetcher;
	private PortsFetcher portsFetcher;
	private FetcherRegistry fetcherRegistry;

	@Before
	public void setUp() throws Exception {
		preferences = Preferences.userRoot().node("ipscan-test");
		preferences.clear();
		
		ipFetcher = new IPFetcher();
		pingFetcher = new PingFetcher(null, null);
		hostnameFetcher = new HostnameFetcher();
		commentFetcher = new CommentFetcher(null);
		portsFetcher = new PortsFetcher(null);
		fetcherRegistry = new FetcherRegistry(asList(ipFetcher, pingFetcher, hostnameFetcher, commentFetcher, portsFetcher), preferences, null);
	}
	
	@After
	public void tearDown() throws Exception {
		preferences.removeNode();
	}

	@Test
	public void testCreate() throws Exception {
		// specified values
		assertEquals(5, fetcherRegistry.getRegisteredFetchers().size());
		// default values
		assertEquals(4, fetcherRegistry.getSelectedFetchers().size());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testModifyRegisteredFetchers() throws Exception {
		fetcherRegistry.getRegisteredFetchers().clear();
	}
	
	@Test
	public void testGetSelectedFetcherIndex() throws Exception {
		assertEquals(0, fetcherRegistry.getSelectedFetcherIndex(ipFetcher.getId()));
		assertEquals(1, fetcherRegistry.getSelectedFetcherIndex(pingFetcher.getId()));
		assertEquals(2, fetcherRegistry.getSelectedFetcherIndex(hostnameFetcher.getId()));
		assertEquals(3, fetcherRegistry.getSelectedFetcherIndex(portsFetcher.getId()));
		assertEquals(-1, fetcherRegistry.getSelectedFetcherIndex(commentFetcher.getId()));
	}
	
	@Test
	public void testLoadPreferences() throws Exception {
		preferences.remove(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS);
		fetcherRegistry = new FetcherRegistry(asList(ipFetcher, hostnameFetcher, commentFetcher), preferences, null);
		assertEquals(4, fetcherRegistry.getSelectedFetchers().size());
		
		preferences.put(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, hostnameFetcher.getId() + "###" + commentFetcher.getId());
		fetcherRegistry = new FetcherRegistry(asList(ipFetcher, hostnameFetcher, commentFetcher), preferences, null);
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertSame(hostnameFetcher, iterator.next());
		assertSame(commentFetcher, iterator.next());
		
		preferences.put(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, "not-existing-fetcher###" + hostnameFetcher.getId());
		fetcherRegistry = new FetcherRegistry(asList(ipFetcher, hostnameFetcher), preferences, null);
		assertEquals(1, fetcherRegistry.getSelectedFetchers().size());
	}
	
	@Test
	public void testUpdateSelectedFetchers() throws Exception {
		// retain only one selected fetcher
		fetcherRegistry.updateSelectedFetchers(new String[] {ipFetcher.getId()});
		assertEquals(1, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(ipFetcher.getId(), ((Fetcher) iterator.next()).getId());
		assertEquals(ipFetcher.getId(), preferences.get(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, null));
		
		// now return a fetcher back
		fetcherRegistry.updateSelectedFetchers(new String[]{commentFetcher.getId(), ipFetcher.getId()});
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(commentFetcher.getId(), ((Fetcher) iterator.next()).getId());
		assertEquals(ipFetcher.getId(), ((Fetcher)iterator.next()).getId());
		assertEquals(commentFetcher.getId() + "###" + ipFetcher.getId(), preferences.get(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, null));
	}
	
	@Test
	public void testListener() throws Exception {
		final boolean listenerWasCalled[] = {false};
		fetcherRegistry.addListener(new FetcherRegistryUpdateListener() {
			public void handleUpdateOfSelectedFetchers(FetcherRegistry fetcherRegistry) {
				assertSame(FetcherRegistryTest.this.fetcherRegistry, fetcherRegistry);
				assertEquals(0, fetcherRegistry.getSelectedFetchers().size());
				listenerWasCalled[0] = true;
			}
		});
		fetcherRegistry.updateSelectedFetchers(new String[]{});
		assertTrue(listenerWasCalled[0]);
	}

	@Test(expected = FetcherException.class)
	public void openUneditableFetcher() {
		fetcherRegistry.openPreferencesEditor(ipFetcher);
	}

	private final static String MESSAGE = "foo bar";
	
	@Test
	public void openPreferencesEditor() {
		Fetcher editableFetcher = mock(Fetcher.class);
		doReturn(EditableFetcherPrefs.class).when(editableFetcher).getPreferencesClass();
		fetcherRegistry = new FetcherRegistry(asList(ipFetcher, editableFetcher), preferences, null);

		EditableFetcherPrefs.calledWithMessage = null;
		fetcherRegistry.openPreferencesEditor(editableFetcher);

		assertSame(MESSAGE, EditableFetcherPrefs.calledWithMessage);
		assertSame(editableFetcher, EditableFetcherPrefs.calledForFetcher);
	}

	public static class EditableFetcherPrefs implements FetcherPrefs {
		private static String calledWithMessage;
		private static Fetcher calledForFetcher;

		public void openFor(Fetcher fetcher) {
			calledWithMessage = MESSAGE;
			calledForFetcher = fetcher;
		}		
	}
}
