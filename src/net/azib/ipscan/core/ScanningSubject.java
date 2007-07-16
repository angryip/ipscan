/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import net.azib.ipscan.core.ScanningResult.ResultType;

/**
 * Scanning subject represents a single scanned
 * IP address and any additional arbitrary parameters,
 * which may be used to cache some intermediate data
 * among different Fetchers. 
 *
 * @author anton
 */
public class ScanningSubject {

	/** The address being scanned */
	private InetAddress address;
	/** Arbitrary parameters for sharing among different (but related) Fetchers */
	private Map<String, Object> parameters;
	/** The result type constant value, can be modified by some Fetchers */
	private ResultType resultType = ResultType.UNKNOWN;
	/** Whether we need to continue scanning or it can be aborted */
	private boolean isScanningAborted = false;
	
	/**
	 * This constructor should only be used by the Scanner class or unit tests.
	 */
	public ScanningSubject(InetAddress address) {
		this.address = address;
		this.parameters = new HashMap<String, Object>();
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
	 * @return true if a fetcher has instructed to abort scanning 
	 */
	public boolean isAddressScanningAborted() {
		return isScanningAborted;
	}

	/**
	 * Can be used to inform the scanner to abort scanning
	 */
	public void abortAddressScanning() {
		this.isScanningAborted = true;
	}
	
}
