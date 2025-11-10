/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.core;

import net.azib.ipscan.config.Labels;

/**
 * Base interface for all plugins.
 *
 * @author Anton Keks
 */
public interface Plugin {
	/**
	 * @return unique ID of the plugin, representing it
	 */
	String getId();
	
	/**
	 * @return localized name of this plugin (usually likely resolved using its id)
	 */
	default String getName() {
		return Labels.getLabel(getId());
	}
}
