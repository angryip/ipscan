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
 * A fetcher that renders a clickable triangle icon in its own column.
 * Clicking the icon launches the IP using its configured default Opener.
 * The column is toggleable like any other fetcher column.
 *
 * @author Anton Keks
 */
public class OpenerLaunchFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.openerLaunch";

	private OpenersConfig openersConfig;
	private DefaultOpenerConfig defaultOpenerConfig;

	public OpenerLaunchFetcher(OpenersConfig openersConfig, DefaultOpenerConfig defaultOpenerConfig) {
		this.openersConfig = openersConfig;
		this.defaultOpenerConfig = defaultOpenerConfig;
	}

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		try {
			// the cell shows a centered triangle character; it launches the default Opener on click
			var ip = subject.getAddress().getHostAddress();
			return defaultOpenerConfig.get(ip) != null ? "▶" : "";
		}
		catch (Exception e) {
			// never let a failure here produce a "n/s" (NotScanned) cell
			return "";
		}
	}
}
