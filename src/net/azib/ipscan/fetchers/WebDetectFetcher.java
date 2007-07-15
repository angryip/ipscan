/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.GlobalConfig;

/**
 * WebDetectFetcher - detects the Web server software running on scanned hosts.
 *
 * @author Anton Keks
 */
public class WebDetectFetcher extends PortTextFetcher {

	public WebDetectFetcher(GlobalConfig globalConfig) {
		super(globalConfig, 80, "HEAD /robots.txt HTTP/1.0\r\n\r\n", "^[Ss]erver:\\s+(.*)$");
	}
	
}
