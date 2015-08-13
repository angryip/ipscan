/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

/**
 * FeederRegistry
 *
 * @author Anton Keks
 */
public interface FeederRegistry<T extends FeederCreator> extends Iterable<T> {
	void select(String feederId);
}
