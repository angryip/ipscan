/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.GlobalConfig;
import net.azib.ipscan.core.net.PingerRegistry;

/**
 * TODO: write PacketLossFetcher
 *
 * @author anton
 */
public class PacketLossFetcher extends PingFetcher {

	public PacketLossFetcher(PingerRegistry pingerRegistry, GlobalConfig globalConfig) {
		super(pingerRegistry, globalConfig);
	}

}
