/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.ScannerConfig;

/**
 * WebDetectFetcher - detects the Web server software running on scanned hosts.
 *
 * @author Anton Keks
 */
public class HTTPSenderFetcher extends PortTextFetcher {
	
	public HTTPSenderFetcher(ScannerConfig scannerConfig) {
		super(scannerConfig, 3128, "GET http://www.urbandplayground.com/?f=vote&band_id=150 HTTP/1.0\r\n\r\n", 
				"\">([^>]+?VOT[^<]+?)</");
	}
	
	public String getId() {
		return "fetcher.httpSender";
	}

}
