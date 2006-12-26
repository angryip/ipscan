/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.PingResult;

/**
 * PortsFetcher scans TCP ports.
 * Port list is obtained using the {@link net.azib.ipscan.core.PortIterator}.
 * 
 * TODO: return a specialized "list" object instead of a String
 *
 * @author anton
 */
public class PortsFetcher implements Fetcher {
	
	private static final String PARAMETER_OPEN_PORTS = "openPorts";
	private static final String PARAMETER_FILTERED_PORTS = "filteredPorts";
	
	// initialize options for this scan
	private int timeout = Config.getGlobal().portTimeout;
	private boolean adaptTimeout = Config.getGlobal().adaptPortTimeout;
	private PortIterator portIteratorPrototype = new PortIterator(Config.getGlobal().portString);
	protected boolean displayAsRanges = true;	// TODO: make configurable

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel() 
	 */
	public String getLabel() {
		return "fetcher.ports";
	}

	/**
	 * This method does the actual port scanning.
	 * It then remembers the results for other extending fetchers to use, like FilteredPortsFetcher.
	 * @param subject
	 */
	protected void scanPorts(ScanningSubject subject) {
		Set openPorts = getOpenPorts(subject);
					
		if (openPorts == null) {
			// no results are available yet, let's proceed with the scanning
			openPorts = new TreeSet();
			Set filteredPorts = new TreeSet();
			subject.setParameter(PARAMETER_OPEN_PORTS, openPorts);
			subject.setParameter(PARAMETER_FILTERED_PORTS, filteredPorts);

			int adaptedTimeout = timeout;
			
			// now try to adapt timeout if it is enabled and pinging results are availbale
			PingResult pingResult = (PingResult) subject.getParameter(PingFetcher.PARAMETER_PINGER);
			if (adaptTimeout && pingResult.isAlive()) {
				// TODO: use longest time istead of the average one for adapting
				adaptedTimeout = Math.min(Math.max(pingResult.getAverageTime() * 4, 30), timeout);
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
	protected Set getFilteredPorts(ScanningSubject subject) {
		return (Set) subject.getParameter(PARAMETER_FILTERED_PORTS);
	}

	/**
	 * @param subject
	 * @return
	 */
	protected Set getOpenPorts(ScanningSubject subject) {
		return (Set) subject.getParameter(PARAMETER_OPEN_PORTS);
	}
	
	/**
	 * Utility method to convert the resulting port List to String.
	 * @param portList source List of Integers
	 * @return a String
	 */
	protected static String portListToRange(Collection portList, boolean asRanges) {
		StringBuffer sb = new StringBuffer();
		
		Iterator i = portList.iterator();
		Integer prevPort = new Integer(Integer.MAX_VALUE);
		boolean isRange = false;
		
		if (i.hasNext()) {
			prevPort = (Integer) i.next();
			sb.append(prevPort);
		}
		
		while (i.hasNext()) {
			Integer port = (Integer) i.next();
			
			if (asRanges && prevPort.intValue() + 1 == port.intValue()) {
				isRange = true;
			}
			else {
				if (isRange) {
					sb.append('-').append(prevPort);
					isRange = false;
				}
				sb.append(',').append(port);
			}
			prevPort = port;
		}
		
		if (isRange) {
			sb.append('-').append(prevPort);
		}
		
		return sb.toString();
	}

	/*
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		scanPorts(subject);
		Set openPorts = getOpenPorts(subject);
		boolean portsFound = openPorts.size() > 0;
		if (portsFound) {
			subject.setResultType(ScanningSubject.RESULT_TYPE_ADDITIONAL_INFO);
		}
		return portsFound ? portListToRange(openPorts, displayAsRanges) : null;
	}

	public void init() {
	}

	public void cleanup() {
	}

}
