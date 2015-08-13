/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.values.NotScanned;
import net.azib.ipscan.core.values.NumericRangeList;
import net.azib.ipscan.gui.fetchers.PortsFetcherPrefs;
import net.azib.ipscan.util.SequenceIterator;
import net.azib.ipscan.util.ThreadResourceBinder;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * PortsFetcher scans TCP ports.
 * Port list is obtained using the {@link net.azib.ipscan.core.PortIterator}.
 *
 * @author Anton Keks
 */
public class PortsFetcher extends AbstractFetcher {
	
	public static final String ID = "fetcher.ports";
	
	private static final String PARAMETER_OPEN_PORTS = "openPorts";
	private static final String PARAMETER_FILTERED_PORTS = "filteredPorts";
	
	private ScannerConfig config;
	private ThreadResourceBinder<Socket> sockets = new ThreadResourceBinder<Socket>();
	
	// initialize preferences for this scan
	private PortIterator portIteratorPrototype;
	protected boolean displayAsRanges = true;	// TODO: make configurable
	
	@Inject public PortsFetcher(ScannerConfig scannerConfig) {
		this.config = scannerConfig;
	}

	public String getId() {
		return ID;
	}

	@Override
	public String getFullName() {
		int numPorts = new PortIterator(config.portString).size();
		return getName() + " [" + numPorts + (config.useRequestedPorts ? "+" : "" ) + "]";
	}
	
	@Override
	public Class<? extends FetcherPrefs> getPreferencesClass() {
		return PortsFetcherPrefs.class;
	}

	/**
	 * This method does the actual port scanning.
	 * It then remembers the results for other extending fetchers to use, like FilteredPortsFetcher.
	 * @param subject the address to scan
	 * @return true if any ports were scanned, false otherwise
	 */
	@SuppressWarnings("unchecked")
	protected boolean scanPorts(ScanningSubject subject) {
		SortedSet<Integer> openPorts = getOpenPorts(subject);
					
		if (openPorts == null) {
			// no results are available yet, let's proceed with the scanning
			openPorts = new TreeSet<Integer>();
			SortedSet<Integer> filteredPorts = new TreeSet<Integer>();
			subject.setParameter(PARAMETER_OPEN_PORTS, openPorts);
			subject.setParameter(PARAMETER_FILTERED_PORTS, filteredPorts);

			int portTimeout = subject.getAdaptedPortTimeout();
			
			// clone port iterator for performance instead of creating for every thread
			Iterator<Integer> portsIterator = portIteratorPrototype.copy();
			if (config.useRequestedPorts && subject.isAnyPortRequested()) {
				// add requested ports to the iteration
				portsIterator = new SequenceIterator<Integer>(portsIterator, subject.requestedPortsIterator());
			}
			if (!portsIterator.hasNext()) {
				// no ports are configured for scanning
				return false;
			}

			while (portsIterator.hasNext() && !Thread.currentThread().isInterrupted()) {
				// TODO: UDP ports?
				Socket socket = sockets.bind(new Socket());
				int port = portsIterator.next();
				try {			
					// set some optimization options
					socket.setReuseAddress(true);
					socket.setReceiveBufferSize(32);
					// now connect
					socket.connect(new InetSocketAddress(subject.getAddress(), port), portTimeout);
					// some more options
					socket.setSoLinger(true, 0);
					socket.setSendBufferSize(16);
					socket.setTcpNoDelay(true);
					
					if (socket.isConnected()) openPorts.add(port);
				}
				catch (SocketTimeoutException e) {
					filteredPorts.add(port);
				}
				catch (IOException e) {
					// connection refused
					assert e instanceof ConnectException : e;
				}
				finally {
          sockets.closeAndUnbind(socket);
				}
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<Integer> getFilteredPorts(ScanningSubject subject) {
		return (SortedSet<Integer>) subject.getParameter(PARAMETER_FILTERED_PORTS);
	}

	@SuppressWarnings("unchecked")
	protected SortedSet<Integer> getOpenPorts(ScanningSubject subject) {
		return (SortedSet<Integer>) subject.getParameter(PARAMETER_OPEN_PORTS);
	}
	
	/*
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		boolean portsScanned = scanPorts(subject);
		if (!portsScanned)
			return NotScanned.VALUE;
		
		SortedSet<Integer> openPorts = getOpenPorts(subject);
		boolean portsFound = openPorts.size() > 0;
		if (portsFound) {
			subject.setResultType(ResultType.WITH_PORTS);
		}
		return portsFound ? new NumericRangeList(openPorts, displayAsRanges) : null;
	}

	public void init() {
		// rebuild port iterator before each scan
		this.portIteratorPrototype = new PortIterator(config.portString);
	}

  @Override
  public void cleanup() {
    sockets.close();
  }
}
