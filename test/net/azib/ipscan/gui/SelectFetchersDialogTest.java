/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.fetchers.FetcherRegistry;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Anton Keks
 */
public class SelectFetchersDialogTest {

	@Test
	public void testSaveFetchersToRegistry() {
		var fetcherRegistry = mock(FetcherRegistry.class);

		var selectFetchersDialog = new SelectFetchersDialog(fetcherRegistry);
		
		selectFetchersDialog.registeredFetcherIdsByNames.put("IP", "fetcher.ip");
		selectFetchersDialog.registeredFetcherIdsByNames.put("Hello", "fetcher.hello");
		selectFetchersDialog.registeredFetcherIdsByNames.put("Blah", "fetcher.blah");
		
		selectFetchersDialog.saveFetchersToRegistry(new String[] {"Blah", "Hello"});
		
		verify(fetcherRegistry).updateSelectedFetchers(new String[] {"fetcher.ip", "fetcher.blah", "fetcher.hello"});
	}
}
