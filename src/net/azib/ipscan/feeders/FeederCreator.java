/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;


/**
 * FeederCreator
 *
 * @author Anton Keks
 */
public interface FeederCreator {
	/**
	 * Initializes a Feeder instance using the parameters, provided by the GUI.
	 * @return initialized feeder instance
	 */
	public Feeder createFeeder();
	
	/**
	 * @return the feeder id
	 */
	public String getFeederId();

	/**
	 * @return the feeder name
	 */
	public String getFeederName();
	
	/**
	 * @return serialized settings to a String
	 */
	public String[] serialize();
	
	/**
	 * Restores previously serialized settings.
	 * @param parts
	 */
	public void unserialize(String ... parts);
	
	/**
	 * @return labels corresponding to parts during serialization.
	 * Used for command-line usage help, etc.
	 */
	public String[] serializePartsLabels();

}
