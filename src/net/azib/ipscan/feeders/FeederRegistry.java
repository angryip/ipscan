/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

/**
 * FeederRegistry
 *
 * @author Anton Keks
 */
public interface FeederRegistry extends Iterable<FeederCreator> {
	void select(String feederId);
}
