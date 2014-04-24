/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.net.PingResult;

/**
 * Scanning subject represents a single scanned
 * IP address and any additional arbitrary parameters,
 * which may be used to cache some intermediate data
 * among different Fetchers. 
 *
 * @author Anton Keks
 */
public class ScanningSubject {
	
	public static final String PARAMETER_PING_RESULT = "pinger";
	
	ScannerConfig config;

	/** The address being scanned */
	private InetAddress address;
	/** The requested ports that the user wishes to put more attention to, can be null. E.g. port 3128 for scanning of proxy servers. */
	private List<Integer> requestedPorts;
	/** Arbitrary parameters for sharing among different (but related) Fetchers */
	private Map<String, Object> parameters;
	/** The result type constant value, can be modified by some Fetchers */
	private ResultType resultType = ResultType.UNKNOWN;
	/** Whether we need to continue scanning or it can be aborted */
	private boolean isAborted = false;
	/** Adapted after pinging port timeout - any fetcher can make use of it */
	int adaptedPortTimeout = -1; 
	
	/**
	 * This constructor should only be used by the Scanner class or unit tests.
	 */
	public ScanningSubject(InetAddress address) {
		this.address = address;
		this.parameters = new HashMap<String, Object>(); // single-threaded access only
		this.config = Config.getConfig().forScanner();
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	/**
	 * Sets a subject specific named parameter.
	 */
	public void setParameter(String name, Object value) {
		parameters.put(name, value);
	}
	
	/**
	 * Gets a subject specific named parameter,
	 * previosly set by setParameter().
	 */
	public Object getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * @return true in case parameter with given name was specified.
	 * This method is useful in case parameter value was null.
	 */
	public boolean hasParameter(String name) {
		return parameters.containsKey(name);
	}

	/**
	 * @return the result type constant value, possibly modified by Fetchers
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * Provides an ability for Fetchers to determine the result type of scanning this particular address
	 * @param resultType enum value
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
	
	/**
	 * @return true if a fetcher has instructed to abort scanning of this address 
	 */
	public boolean isAddressAborted() {
		return isAborted;
	}

	/**
	 * Can be used to inform the scanner to abort scanning of this address
	 */
	public void abortAddressScanning() {
		this.isAborted = true;
	}
	
	public boolean isAnyPortRequested() {
		return requestedPorts != null;
	}
	
	/**
	 * @return ports that the user wishes to pay attention to, e.g. 3128 for proxies, or null.
	 */
	public Iterator<Integer> requestedPortsIterator() {
		return requestedPorts == null ? null : requestedPorts.iterator();
	}

	/**
	 * @param requestedPort the port that user wants to scan
	 */
	public void addRequestedPort(Integer requestedPort) {
		if (requestedPorts == null)
			requestedPorts = new ArrayList<Integer>();
		requestedPorts.add(requestedPort);
	}

	/**
	 * @return adapted port timeout for this host if available
	 */
	public int getAdaptedPortTimeout() {
		// see if it is already computed
		if (adaptedPortTimeout > 0) 
			return adaptedPortTimeout;
		
		// try to adapt timeout if it is enabled and pinging results are available
		PingResult pingResult = (PingResult) getParameter(PARAMETER_PING_RESULT);
		if (pingResult != null) {
			if (config.adaptPortTimeout && pingResult.isTimeoutAdaptationAllowed()) {
				adaptedPortTimeout = Math.min(Math.max(pingResult.getLongestTime() * 3, config.minPortTimeout), config.portTimeout);
				return adaptedPortTimeout;
			}
		}
		// if no pinging results are available yet, return the full timeout
		return config.portTimeout;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(address.getHostAddress());
		if (requestedPorts != null) {
			sb.append(':');
			for (Integer port : requestedPorts)
				sb.append(port).append(',');
			if (sb.charAt(sb.length()-1) == ',')
				sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
}
