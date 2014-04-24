/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import static org.mockito.Mockito.*;

import net.azib.ipscan.fetchers.FetcherRegistry;

import org.junit.Test;

/**
 * @author Anton Keks
 */
public class SelectFetchersDialogTest {

	@Test
	public void testSaveFetchersToRegistry() {
		FetcherRegistry fetcherRegistry = mock(FetcherRegistry.class);

		SelectFetchersDialog selectFetchersDialog = new SelectFetchersDialog(fetcherRegistry);
		
		selectFetchersDialog.registeredFetcherIdsByNames.put("IP", "fetcher.ip");
		selectFetchersDialog.registeredFetcherIdsByNames.put("Hello", "fetcher.hello");
		selectFetchersDialog.registeredFetcherIdsByNames.put("Blah", "fetcher.blah");
		
		selectFetchersDialog.saveFetchersToRegistry(new String[] {"Blah", "Hello"});
		
		verify(fetcherRegistry).updateSelectedFetchers(new String[] {"fetcher.ip", "fetcher.blah", "fetcher.hello"});
	}
}
