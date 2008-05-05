/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.fetchers.HTTPSenderFetcher;
import net.azib.ipscan.gui.InputDialog;

/**
 * HTTPSenderFetcherPrefs
 *
 * @author Anton Keks
 */
public class HTTPSenderFetcherPrefs implements Runnable {
	
	private HTTPSenderFetcher fetcher;
	
	public HTTPSenderFetcherPrefs(HTTPSenderFetcher fetcher) {
		this.fetcher = fetcher;
	}

	public void run() {
		new InputDialog(fetcher.getName(), "hello").open(fetcher.getTextToSend());
	}

}
