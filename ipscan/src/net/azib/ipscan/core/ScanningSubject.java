/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;


import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Scanning subject represents a single scanned
 * IP address and any additional arbitrary parameters,
 * which may be used to cache some intermediate data
 * among different Fetchers. 
 *
 * @author anton
 */
public class ScanningSubject {

	// constants for result type (they can be modified by some Fetchers)
	public static final int RESULT_TYPE_UNKNOWN = 0;
	public static final int RESULT_TYPE_DEAD = 1;
	public static final int RESULT_TYPE_ALIVE = 2;
	public static final int RESULT_TYPE_ADDITIONAL_INFO = 3;

	/** The address being scanned */
	private InetAddress address;
	/** Arbitrary parameters for sharing among different (but related) Fetchers */
	private Map parameters;
	/** The result type constant value, can be modified by some Fetchers */
	private int resultType = RESULT_TYPE_UNKNOWN;
	/** Whether we need to continue scanning or it can be aborted */
	private boolean isScanningAborted = false;
	
	/**
	 * This constructor should only be used by the Scanner class or unit tests.
	 */
	public ScanningSubject(InetAddress address) {
		this.address = address;
		this.parameters = new HashMap();
	}
	
	public InetAddress getIPAddress() {
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
	public int getResultType() {
		return resultType;
	}

	/**
	 * Provides an ability for Fetchers to determine the result type of scanning this particular address
	 * @param resultType constant value
	 */
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
	
	/**
	 * @return true if a fetcher has instructed to abort scanning 
	 */
	public boolean isScanningAborted() {
		return isScanningAborted;
	}

	/**
	 * Can be used to inform the scanner to abort scanning
	 */
	public void abortScanning() {
		this.isScanningAborted = true;
	}
	
}
