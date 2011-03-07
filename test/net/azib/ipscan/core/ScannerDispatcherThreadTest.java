/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.IPFetcher;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.objenesis.ObjenesisHelper;

/**
 * ScannerThreadTest
 *
 * @author Anton Keks
 */
public class ScannerDispatcherThreadTest {
	
	@Test
	public void testConstruction() throws Exception {
		FetcherRegistry registry = mock(FetcherRegistry.class);
		when(registry.getSelectedFetchers()).thenReturn(Collections.<Fetcher>singleton(new IPFetcher()));
		Feeder feeder = mock(Feeder.class);
		when(feeder.getInfo()).thenReturn("info");
		when(feeder.getName()).thenReturn("text.ip");

		ScanningResultList scanningResults = new ScanningResultList(registry);
		scanningResults.info = new ScanInfo(); // initialize info so we can add a dummy result
		scanningResults.registerAtIndex(0, scanningResults.createResult(InetAddress.getLocalHost()));
		
		ScannerConfig config = mock(ScannerConfig.class);
		config.maxThreads = 10;
		
		ScannerDispatcherThread thread = new ScannerDispatcherThread(feeder, new Scanner(registry), null, null, scanningResults, config, null);

		assertTrue("ScannerThread should not clear the results - otherwise rescanning will not work", 
				    scanningResults.areResultsAvailable());
		
		assertEquals(thread.getClass().getSimpleName(), thread.getName());
		assertTrue(thread.isDaemon());
		assertEquals(config.maxThreads, ((ThreadPoolExecutor)thread.threadPool).getMaximumPoolSize());
		assertEquals(thread, ((ThreadPoolExecutor) thread.threadPool).getThreadFactory());
	}
	
	@Test
	public void threadFactoryProducesDaemons() throws Exception {
		ScannerDispatcherThread thread = (ScannerDispatcherThread) ObjenesisHelper.newInstance(ScannerDispatcherThread.class);
		thread.threadGroup = new ThreadGroup("foo");
		Thread t = thread.newThread(mock(Runnable.class));
		assertTrue(t.isDaemon());
		assertSame(thread.threadGroup, t.getThreadGroup());
	}
}
