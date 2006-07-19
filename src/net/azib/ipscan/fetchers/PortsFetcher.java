/**
 * 
 */
package net.azib.ipscan.fetchers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.net.Pinger;

/**
 * PortsFetcher scans TCP ports.
 * Port list is obtained using the {@link net.azib.ipscan.core.PortIterator}.
 *
 * @author anton
 */
public class PortsFetcher implements Fetcher {
	
	public static final String PARAMETER_PORTS = "ports";
	
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
	 * It then remembers the results for other exteding fetchers to use, like FilteredPortsFetcher.
	 * @param subject
	 */
	protected SortedSet scanPorts(ScanningSubject subject) {
		TreeSet portsList = (TreeSet) subject.getParameter(PARAMETER_PORTS);
					
		if (portsList == null) {
			// no results are available yet, let's proceed with the scanning
			portsList = new TreeSet();
			subject.setParameter(PARAMETER_PORTS, portsList);

			int adaptedTimeout = timeout;
			
			// now try to adapt timeout if it is enabled and pinging results are availbale
			Pinger pinger = (Pinger) subject.getParameter(PingFetcher.PARAMETER_PINGER);
			if (adaptTimeout && pinger != null && !pinger.isTimeout()) {
				adaptedTimeout = Math.min(Math.max(pinger.getAverageTime() * 4, 30), timeout);
			}			
			
			Socket socket = null;
			// TODO: clone port iterator for performance instead of creating for every thread separately
			for (PortIterator i = portIteratorPrototype.copy(); i.hasNext(); ) {
				try {				
					// TODO: UDP ports?
					// TODO: reuse sockets?
					socket = new Socket();
					int port = i.next();
					socket.connect(new InetSocketAddress(subject.getIPAddress(), port), adaptedTimeout);
					if (socket.isConnected()) {
						portsList.add(new Integer(port));
					}
				}
				catch (IOException e) {
					// connection refused stuff
					assert e instanceof ConnectException : e;
					// TODO: timeouts should be processed
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
		return portsList;
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

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public String scan(ScanningSubject subject) {
		SortedSet portsList = scanPorts(subject);
		boolean portsFound = portsList.size() > 0;
		if (portsFound) {
			subject.setResultType(ScanningSubject.RESULT_TYPE_ADDITIONAL_INFO);
		}
		return portsFound ? portListToRange(portsList, displayAsRanges) : null;
	}
}
