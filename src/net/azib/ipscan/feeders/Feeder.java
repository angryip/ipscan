/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.core.plugins.Pluggable;

/**
 * Interface of a Feeder, which is used to feed scanner with IP addresses.
 * Basically, classes implementing Feeder must provide an algorithm of 
 * sequentially generating the list of scanned IP addresses.
 * 
 * Implementations should be 'immutable', i.e. once created, they should not
 * change their internal parameters (getInfo() must always return the same value).
 * 
 * A new instance of Feeder will be created for each scan, passing the required
 * parameters to constructor. Default constructor should also be provided in order 
 * to query name and id of the Feeder.
 * 
 * @author Anton Keks
 */
public interface Feeder extends Pluggable {
	
	/**
	 * @return true in case there are more IPs left for processing
	 */
	public boolean hasNext();
	
	/**
	 * @return the next IP for processing
	 */
	public ScanningSubject next();
	
	/**
	 * @return value from 0 to 100, describing the amount of work already done
	 */
	public int percentageComplete();

	/**
	 * @return information about feeder's current settings.
	 * Used for creation of Favorites, saving to file, etc.
	 */
	public String getInfo();	
}
