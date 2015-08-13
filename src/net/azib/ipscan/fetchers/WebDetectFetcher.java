/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;

import javax.inject.Inject;

/**
 * WebDetectFetcher - detects the Web server software running on scanned hosts.
 *
 * @author Anton Keks
 */
public class WebDetectFetcher extends PortTextFetcher {

	@Inject public WebDetectFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig, 80, "HEAD /robots.txt HTTP/1.0\r\n\r\n", "^[Ss]erver:\\s+(.*)$");
	}
	
	public String getId() {
		return "fetcher.webDetect";
	}
	
}
