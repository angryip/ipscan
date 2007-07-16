/**
 * 
 */
package net.azib.ipscan.gui;

import static org.easymock.EasyMock.*;

import net.azib.ipscan.fetchers.FetcherRegistry;

import org.junit.Test;

/**
 * @author Anton Keks Keks
 *
 */
public class SelectFetchersDialogTest {

	@Test @SuppressWarnings("unchecked")
	public void testSaveFetchersToRegistry() {
		FetcherRegistry fetcherRegistry = createMock(FetcherRegistry.class);
		fetcherRegistry.updateSelectedFetchers(aryEq(new String[] {"fetcher.ip", "fetcher.blah", "fetcher.hello"}));
		replay(fetcherRegistry);
		
		SelectFetchersDialog selectFetchersDialog = new SelectFetchersDialog(fetcherRegistry);
		
		selectFetchersDialog.registeredFetcherLabelsByNames.put("IP", "fetcher.ip");
		selectFetchersDialog.registeredFetcherLabelsByNames.put("Hello", "fetcher.hello");
		selectFetchersDialog.registeredFetcherLabelsByNames.put("Blah", "fetcher.blah");
		
		selectFetchersDialog.saveFetchersToRegistry(new String[] {"Blah", "Hello"});
		
		verify(fetcherRegistry);
	}

}
