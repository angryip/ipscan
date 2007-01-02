/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;
import net.azib.ipscan.core.values.NumericListValue;

/**
 * PortsFetcher scans TCP ports.
 * Port list is obtained using the {@link net.azib.ipscan.core.PortIterator}.
 *
 * @author anton
 */
public class PortsFetcher implements Fetcher {
	
	private static final String PARAMETER_OPEN_PORTS = "openPorts";
	private static final String PARAMETER_FILTERED_PORTS = "filteredPorts";
	
	private GlobalConfig config;
	
	// initialize options for this scan
	private PortIterator portIteratorPrototype;
	protected boolean displayAsRanges = true;	// TODO: make configurable
	
	public PortsFetcher(GlobalConfig globalConfig) {
		this.config = globalConfig;
		this.portIteratorPrototype = new PortIterator(config.portString);
	}

	public String getLabel() {
		return "fetcher.ports";
	}

	/**
	 * This method does the actual port scanning.
	 * It then remembers the results for other extending fetchers to use, like FilteredPortsFetcher.
	 * @param subject
	 */
	protected void scanPorts(ScanningSubject subject) {
		NumericListValue openPorts = getOpenPorts(subject);
					
		if (openPorts == null) {
			// no results are available yet, let's proceed with the scanning
			openPorts = new NumericListValue(displayAsRanges);
			Set filteredPorts = new NumericListValue(displayAsRanges);
			subject.setParameter(PARAMETER_OPEN_PORTS, openPorts);
			subject.setParameter(PARAMETER_FILTERED_PORTS, filteredPorts);

			int adaptedTimeout = config.portTimeout;
			
			// now try to adapt timeout if it is enabled and pinging results are availbale
			PingResult pingResult = (PingResult) subject.getParameter(PingFetcher.PARAMETER_PINGER);
			if (config.adaptPortTimeout && pingResult.isAlive()) {
				// TODO: use longest time istead of the average one for adapting
				adaptedTimeout = Math.min(Math.max(pingResult.getAverageTime() * 4, 30), config.portTimeout);
			}
			
			Socket socket = null;
			// clone port iterator for performance instead of creating for every thread
			for (PortIterator i = portIteratorPrototype.copy(); i.hasNext(); ) {
				// TODO: UDP ports?
				// TODO: reuse sockets?
				socket = new Socket();
				int port = i.next();
				try {				
					socket.connect(new InetSocketAddress(subject.getIPAddress(), port), adaptedTimeout);
					if (socket.isConnected()) {
						openPorts.add(new Integer(port));
					}
				}
				catch (SocketTimeoutException e) {
					filteredPorts.add(new Integer(port));
				}
				catch (IOException e) {
					// connection refused
					assert e instanceof ConnectException : e;
				}
				finally {
					if (socket != null) {
						try {
							socket.close();
						}
						catch (IOException e) {}
					}
				}
			}
		}
	}

	/**
	 * @param subject
	 * @return
	 */
	protected NumericListValue getFilteredPorts(ScanningSubject subject) {
		return (NumericListValue) subject.getParameter(PARAMETER_FILTERED_PORTS);
	}

	/**
	 * @param subject
	 * @return
	 */
	protected NumericListValue getOpenPorts(ScanningSubject subject) {
		return (NumericListValue) subject.getParameter(PARAMETER_OPEN_PORTS);
	}
	
	/*
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		scanPorts(subject);
		NumericListValue openPorts = getOpenPorts(subject);
		boolean portsFound = openPorts.size() > 0;
		if (portsFound) {
			subject.setResultType(ScanningSubject.RESULT_TYPE_ADDITIONAL_INFO);
		}
		return portsFound ? openPorts : null;
	}

	public void init() {
	}

	public void cleanup() {
	}

}
