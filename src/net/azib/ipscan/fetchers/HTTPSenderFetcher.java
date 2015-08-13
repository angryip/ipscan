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
public class HTTPSenderFetcher extends PortTextFetcher {
	
	@Inject public HTTPSenderFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig, 3128, "GET http://www.google.com HTTP/1.0\r\n\r\n", "Location: (https?.*)$");
	}
	
	public String getId() {
		return "fetcher.httpSender";
	}

}
