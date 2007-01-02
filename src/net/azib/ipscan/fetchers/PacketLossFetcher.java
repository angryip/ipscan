/**
 * 
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
