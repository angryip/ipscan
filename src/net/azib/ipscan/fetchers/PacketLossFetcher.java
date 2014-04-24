/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.net.PingerRegistry;

/**
 * TODO: write PacketLossFetcher
 *
 * @author Anton Keks
 */
public class PacketLossFetcher extends PingFetcher {

	public PacketLossFetcher(PingerRegistry pingerRegistry, ScannerConfig scannerConfig) {
		super(pingerRegistry, scannerConfig);
	}

}
