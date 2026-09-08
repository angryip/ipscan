/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.DefaultOpenerConfig;
import net.azib.ipscan.config.OpenersConfig;
import net.azib.ipscan.core.ScanningSubject;

/**
 * A fetcher that shows the default Opener configured for every IP.
 * If no default Opener is set for an IP, a question mark ("?") is shown.
 *
 * @author Anton Keks
 */
public class OpenerColumnFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.opener";

	private OpenersConfig openersConfig;
	private DefaultOpenerConfig defaultOpenerConfig;

	public OpenerColumnFetcher(OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig) {
		this.openersConfig = openersConfig;
		this.defaultOpenerConfig = defaultOpenerConfig;
	}

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		var ip = subject.getAddress().getHostAddress();
		var openerName = defaultOpenerConfig.get(ip);
		return openerName != null ? openerName : "—";
	}
}
