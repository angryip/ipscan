/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Collections;

import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * ScannerThreadTest
 *
 * @author Anton Keks
 */
public class ScannerThreadTest {
	
	@Test
	public void testConstructor() throws Exception {
		FetcherRegistry registry = createMock(FetcherRegistry.class);
		expect(registry.getSelectedFetchers()).andReturn(Collections.<Fetcher>singleton(new IPFetcher())).anyTimes();
		Feeder feeder = createMock(Feeder.class);
		expect(feeder.getInfo()).andReturn("info");
		expect(feeder.getLabel()).andReturn("text.ip");
		replay(registry, feeder);
		
		ScanningResultList scanningResults = new ScanningResultList(registry);
		scanningResults.info = new ScanInfo(); // initialize info so we can add a dummy result
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		
		ScannerThread thread = new ScannerThread(feeder, new Scanner(registry), null, null, scanningResults, null, null);

		assertTrue("ScannerThread should not clear the results - otherwise rescanning will not work", 
				    scanningResults.areResultsAvailable());
		assertEquals("Scanner Thread", thread.getName());
		assertTrue(thread.isDaemon());
		verify(registry, feeder);
	}

}
