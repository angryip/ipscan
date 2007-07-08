/**
 * 
 */
package net.azib.ipscan.feeders;

import java.net.InetAddress;

/**
 * Interface of a Feeder, which is used to feed scanner with IP addresses.
 * Basically, classes implementing Feeder must provide an algorithm of 
 * sequentially generating the list of scanned IP addresses.
 * 
 * Feeders are created with an empty constructor only once in the applications life time.
 * All subsequent calls to the {@link #initialize(String[])} should reset the state of
 * the Feeder and begin a new "feeding" process.
 * 
 * @author anton
 */
public interface Feeder {
	
	/**
	 * @return label ID, representing the name of this Feeder
	 */	
	public String getLabel();
	
	/**
	 * Initializes the Feeder, passing Strings as initialization parameters.
	 * This method is used for resetting the state of the Feeder (similar to a constructor)
	 * in both GUI and console interfaces.
	 * 
	 * @param params the meaning and the number of these Strings depend on the implementation.
	 * @return the number of consumed parameters
	 */
	public int initialize(String ... params);
	
	/**
	 * @return true in case there are more IPs left for processing
	 */
	public boolean hasNext();
	
	/**
	 * @return the next IP for processing
	 */
	public InetAddress next();
	
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
