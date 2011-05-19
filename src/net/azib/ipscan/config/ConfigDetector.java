/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class detects some important config parameter values
 * that work reliably on the given machine, like maximum number of threads.
 *
 * @author Anton Keks
 */
public class ConfigDetector {
	
	private static final int CONNECTS_PER_THREAD = 16;
	private static final double SUCCESS_PROBABILITY = 0.2;
	
	private static Logger logger;
	private ScannerConfig config;
	private DetectorCallback callback;
	private AtomicInteger expectedConnects;
	private AtomicInteger actualConnects;
	
	public ConfigDetector(ScannerConfig config) {
		this.config = config;
	}
	
	public void setCallback(DetectorCallback callback) {
		this.callback = callback;
	}

	public void detectMaxThreads(InetSocketAddress socketAddress) {
		// init it here to reduce stuff needed to do in constructor
		logger = LoggerFactory.getLogger();
		
		expectedConnects = new AtomicInteger();
		actualConnects = new AtomicInteger();
				
		List<Thread> threads = new LinkedList<Thread>();

		// first ensure that successful connections can be opened 
		for (int i = 0; i < config.maxThreads; i++) {
			Thread t = new SocketThread(socketAddress);
			threads.add(t);
			t.start();
		}
		join(threads);
	}

	private void join(List<Thread> threads) {
		try {
			for (Thread t : threads) {
				t.join();
			}
			threads.clear();
		}
		catch (InterruptedException e) {
		}
	}
	
	public int getInitialConnectCount() {
		return config.maxThreads * CONNECTS_PER_THREAD;
	}
	
	public int getInitialSuccessCount() {
		return (int)(getInitialConnectCount() * SUCCESS_PROBABILITY);
	}
	
	public int getExpectedSuccessfulConnectCount() {
		return expectedConnects.intValue();
	}
	
	public int getActualSuccessfulConnectCount() {
		return actualConnects.intValue();
	}
			
	public interface DetectorCallback {
		void onDetectorTry();
		void onDetectorSuccess();
	}
	
	class SocketThread extends Thread {
		private InetSocketAddress socketAddress;
		
		public SocketThread(InetSocketAddress socketAddress) {
			this.socketAddress = socketAddress;
		}

		public void run() {
			for (int i = 0; i < CONNECTS_PER_THREAD; i++) {
				if (callback != null)
					callback.onDetectorTry();
				Socket s = new Socket();
				try {
					s.setSoTimeout(config.portTimeout);
					s.setTcpNoDelay(true);
					s.setSoLinger(true, 0);
					if (Math.random() > (1.0 - SUCCESS_PROBABILITY)) {
						expectedConnects.incrementAndGet();
						s.connect(socketAddress, config.portTimeout);
					}
					else {
						s.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[] {(byte)192, (byte)168, (byte)(Math.random()*255), (byte)(Math.random()*255)}), 61493+(int)(Math.random()*200)), config.portTimeout);
					}
					
					actualConnects.incrementAndGet();
					if (callback != null) {
						callback.onDetectorSuccess();
					}
					sleep(10000);										
				}
				catch (SocketTimeoutException e) {
					// ignore
				}
				catch (Exception e) {
					logger.log(Level.FINE, "Failure: " + e);
				}
				finally {
					try {
						s.close();
					}
					catch (IOException e) {					
					}
				}
			}
		}
	}
}
