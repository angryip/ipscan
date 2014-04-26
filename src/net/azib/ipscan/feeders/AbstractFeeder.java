/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.feeders;

import net.azib.ipscan.config.Labels;

/**
 * Helper base class for built-in Feeders
 *
 * @author Anton Keks
 */
public abstract class AbstractFeeder implements Feeder {
	protected long totalCount;
	protected long completedCount;

	public String getName() {
		return Labels.getLabel(getId());
	}

	@Override
	public long getTotalCount() {
		return totalCount;
	}

	@Override
	public long getCompletedCount() {
		return completedCount;
	}

	@Override
	public int percentageComplete() {
		return (int)(getCompletedCount() * 100 / getTotalCount());
	}

	@Override
	public String toString() {
		return getName() + ": " + getInfo();
	}
}
