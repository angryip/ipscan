/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.config;

import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;

/**
 * CommandLineProcessor
 *
 * @author Anton Keks
 */
public class CommandLineProcessor {
	
	private AbstractFeederGUI[] feeders;
	private ExporterRegistry exporters;
	
	public CommandLineProcessor(AbstractFeederGUI[] feeders, ExporterRegistry exporters) {
		this.feeders = feeders;
		this.exporters = exporters;
	}

	@Override
	public String toString() {
		StringBuilder usage = new StringBuilder();
		usage.append("Command-line support is not yet implemented...\n\nAvailable feeders:\n");
		for (AbstractFeederGUI feeder : feeders) {
			usage.append(feeder.getFeederName()).append('\n');
		}
		return usage.toString();
	}
	
}
