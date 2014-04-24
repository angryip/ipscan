/**
 * 
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.ScanningSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Iterator;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

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
		fetcherRegistry = new FetcherRegistry(new Fetcher[] {ipFetcher, pingFetcher, hostnameFetcher, commentFetcher, portsFetcher}, preferences, null);
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
		fetcherRegistry = new FetcherRegistry(new Fetcher[] {ipFetcher, hostnameFetcher, commentFetcher}, preferences, null);
		assertEquals(4, fetcherRegistry.getSelectedFetchers().size());
		
		preferences.put(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, hostnameFetcher.getId() + "###" + commentFetcher.getId());
		fetcherRegistry = new FetcherRegistry(new Fetcher[] {ipFetcher, hostnameFetcher, commentFetcher}, preferences, null);
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertSame(hostnameFetcher, iterator.next());
		assertSame(commentFetcher, iterator.next());
		
		preferences.put(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, "not-existing-fetcher###" + hostnameFetcher.getId());
		fetcherRegistry = new FetcherRegistry(new Fetcher[] {ipFetcher, hostnameFetcher}, preferences, null);
		assertEquals(1, fetcherRegistry.getSelectedFetchers().size());
	}
	
	@Test
	public void testUpdateSelectedFetchers() throws Exception {
		// retain only one selected fetcher
		fetcherRegistry.updateSelectedFetchers(new String[] {ipFetcher.getId()});
		assertEquals(1, fetcherRegistry.getSelectedFetchers().size());
		Iterator<?> iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(ipFetcher.getId(), ((Fetcher)iterator.next()).getId());
		assertEquals(ipFetcher.getId(), preferences.get(FetcherRegistry.PREFERENCE_SELECTED_FETCHERS, null));
		
		// now return a fetcher back
		fetcherRegistry.updateSelectedFetchers(new String[] {commentFetcher.getId(), ipFetcher.getId()});
		assertEquals(2, fetcherRegistry.getSelectedFetchers().size());
		iterator = fetcherRegistry.getSelectedFetchers().iterator();
		assertEquals(commentFetcher.getId(), ((Fetcher)iterator.next()).getId());
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
		fetcherRegistry.updateSelectedFetchers(new String[] {});
		assertTrue(listenerWasCalled[0]);
	}
	
	@Test
	public void testOpenPreferencesEditor() throws Exception {
		String message = "foo bar";
		MutablePicoContainer container = new DefaultPicoContainer();
		container.registerComponentInstance(message);
		
		Fetcher editableFetcher = new EditableFetcher();
		fetcherRegistry = new FetcherRegistry(new Fetcher[] {ipFetcher, editableFetcher}, preferences, container);
		
		EditableFetcherPrefs.calledWithMessage = null;
		fetcherRegistry.openPreferencesEditor(editableFetcher);		
		assertSame(message, EditableFetcherPrefs.calledWithMessage);
		assertSame(editableFetcher, EditableFetcherPrefs.calledForFetcher);

		try {
			fetcherRegistry.openPreferencesEditor(ipFetcher);
			fail("This fetcher is not editable");
		}
		catch (FetcherException e) {
		}
	}
	
	public static class EditableFetcher extends AbstractFetcher {
		public String getId() {
			return null;
		}
		public Object scan(ScanningSubject subject) {
			return null;
		}
		@Override
		public Class<? extends FetcherPrefs> getPreferencesClass() {
			return EditableFetcherPrefs.class;
		}
	}
	
	public static class EditableFetcherPrefs implements FetcherPrefs {
		private static String calledWithMessage;
		private static Fetcher calledForFetcher;
		
		private String message;

		public EditableFetcherPrefs(String message) {
			this.message = message;
		}

		public void openFor(Fetcher fetcher) {
			calledWithMessage = message;
			calledForFetcher = fetcher;
		}		
	}
}
