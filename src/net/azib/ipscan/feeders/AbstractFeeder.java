/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;

/**
 * Helper base class for built-in Feeders
 *
 * @author Anton Keks
 */
public abstract class AbstractFeeder implements Feeder {
	
	public String getName() {
		return Labels.getLabel(getId());
	}

	@Override
	public String toString() {
		return getName() + ": " + getInfo();
	}
}
