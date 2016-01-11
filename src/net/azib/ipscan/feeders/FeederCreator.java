/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
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
	Feeder createFeeder();
	
	/**
	 * @return the feeder id
	 */
	String getFeederId();

	/**
	 * @return the feeder name
	 */
	String getFeederName();
	
	/**
	 * @return serialized settings to a String
	 */
	String[] serialize();
	
	/**
	 * Restores previously serialized settings.
	 * @param parts
	 */
	void unserialize(String... parts);
	
	/**
	 * @return labels corresponding to parts during serialization.
	 * Used for command-line usage help, etc.
	 */
	String[] serializePartsLabels();

}
