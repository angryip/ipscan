/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;
import org.junit.Test;
import org.objenesis.ObjenesisHelper;

import java.net.InetAddress;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ScannerThreadTest
 *
 * @author Anton Keks
 */
public class ScannerDispatcherThreadTest {
	
	@Test
	public void testConstruction() throws Exception {
		var registry = mock(FetcherRegistry.class);
		when(registry.getSelectedFetchers()).thenReturn(Collections.singleton(new IPFetcher()));
		var feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn("info");
		when(feeder.getName()).thenReturn("text.ip");

		var scanningResults = new ScanningResultList(registry);
		scanningResults.info = new ScanInfo(); // initialize info so we can add a dummy result
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));

		var config = mock(ScannerConfig.class);
		config.maxThreads = 10;

		var thread = new ScannerDispatcherThread(feeder, new Scanner(registry), null, null, scanningResults, config, null);

		assertTrue("ScannerThread should not clear the results - otherwise rescanning will not work", 
				    scanningResults.areResultsAvailable());
		
		assertEquals(thread.getClass().getSimpleName(), thread.getName());
		assertTrue(thread.isDaemon());
		assertEquals(config.maxThreads, ((ThreadPoolExecutor)thread.threadPool).getMaximumPoolSize());
		assertEquals(thread, ((ThreadPoolExecutor) thread.threadPool).getThreadFactory());
	}
	
	@Test
	public void threadFactoryProducesDaemons() throws Exception {
		var thread = ObjenesisHelper.newInstance(ScannerDispatcherThread.class);
		thread.threadGroup = new ThreadGroup("foo");
		var t = thread.newThread(mock(Runnable.class));
		assertTrue(t.isDaemon());
		assertSame(thread.threadGroup, t.getThreadGroup());
	}
}
