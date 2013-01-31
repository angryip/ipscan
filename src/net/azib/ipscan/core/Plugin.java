/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core;

/**
 * Base interface for all plugins.
 *
 * @author Anton Keks
 */
public interface Plugin {
	/**
	 * @return unique ID of the pluggable, representing it
	 */
	public String getId();
	
	/**
	 * @return localized name of this pluggable (most likely resolved using it's id)
	 */
	public String getName();
	
}
