/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class detects some important config parameter values
 * that work reliably on the given machine, like maximum number of threads.
 *
 * @author Anton Keks
 */
public class ConfigDetector {
	
	private static Logger logger;
	private GlobalConfig config;
	private DetectorCallback callback;
	private int numConnects;
	private int numCorrectReads;
	
	public ConfigDetector(GlobalConfig config) {
		this.config = config;
	}
	
	public void setCallback(DetectorCallback callback) {
		this.callback = callback;
	}

	public void detectMaxThreads() {
		// init it here to reduce stuff needed to do in constructor
		logger = LoggerFactory.getLogger();
		
		numConnects = 0;
		numCorrectReads = 0;
		
		final DetectionServerThread serverThread = new DetectionServerThread();
		serverThread.setDaemon(true);
		serverThread.start();
		synchronized (serverThread) {
			try {
				// wait for port value to become available
				serverThread.wait(10000);
			}
			catch (InterruptedException e) {
			}
		}
		
		List<Thread> threads = new LinkedList<Thread>();

		try {
			final InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), serverThread.port);

			for (int i = 0; i < config.maxThreads; i++) {
				if (callback != null)
					callback.onDetectorTry();
				Thread t = new Thread() {
					public void run() {
						Socket s = new Socket();
						try {
							s.setSoTimeout(config.portTimeout);
							s.setTcpNoDelay(true);
							s.setSoLinger(true, 0);
							s.connect(socketAddress, config.portTimeout);
							numConnects++;
							byte[] buf = new byte[64];
							s.getInputStream().read(buf);
							if (buf[0] == 'H') {
								numCorrectReads++;
								if (callback != null)
									callback.onDetectorSuccess();
							}
							s.close();						
						}
						catch (Exception e) {
							logger.log(Level.FINE, "Failure on local port " + s.getLocalPort());
						}
					}
				};
				threads.add(t);
				t.start();
			}
		}
		catch (UnknownHostException e1) {
			// getLocalHost should not fail
			LoggerFactory.getLogger().log(Level.WARNING, "", e1);
		}
		
		// by this time, all the thread must be finished
		try {
			for (Thread t : threads) {
				t.join();
			}
		}
		catch (InterruptedException e) {
		}
		serverThread.interrupt();
	}
	
	public int getSuccessfulConnectCount() {
		return numConnects;
	}
	
	public int getSuccessfulDataReadCount() {
		return numCorrectReads;
	}
	
	static class DetectionServerThread extends Thread {
		int port;
		
		public void run() {
			try {
				ServerSocket server = new ServerSocket(0);
				//server.setSoTimeout();
				port = server.getLocalPort();
				synchronized (this) {
					// tell the other thread that port value is now available
					this.notify();
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "Started fake server on port " + port);
				}
				while (!interrupted()) {
					final Socket s = server.accept();
					new Thread() {
						public void run() {
							try {
								s.setTcpNoDelay(true);
								s.setSoLinger(true, 0);
								OutputStream stream = s.getOutputStream();
								stream.write(("Hello " + s.getPort()).getBytes());
								stream.flush();
								sleep(10000);
								s.close();
							}
							catch (Exception e) {
								logger.log(Level.FINER, "On port " + s.getPort(), e);
							}
						}
					}.start();
				}
				logger.log(Level.FINE, "Stopped fake server");
			}
			catch (IOException e) {
				logger.log(Level.FINE, null, e);
			}
		}
	}
	
	public interface DetectorCallback {
		void onDetectorTry();
		void onDetectorSuccess();
	}
}
