/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.values.Empty;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.HostnameFetcher;

/**
 * OpenerLauncher
 *
 * @author Anton Keks
 */
public class OpenerLauncher {
	
	private final FetcherRegistry fetcherRegistry;
	private final ScanningResultList scanningResults;
	
	public OpenerLauncher(FetcherRegistry fetcherRegistry, ScanningResultList scanningResults) {
		this.fetcherRegistry = fetcherRegistry;
		this.scanningResults = scanningResults;
	}

	public void launch(Opener opener, int selectedItem) {
		String openerString = prepareOpenerStringForItem(opener.execString, selectedItem);
		
		// check for URLs
		if (openerString.startsWith("http:") || openerString.startsWith("https:") || openerString.startsWith("ftp:") || openerString.startsWith("mailto:") || openerString.startsWith("\\\\")) {
			BrowserLauncher.openURL(openerString);
		}
		else {
			// run a process here
			try {
				if (opener.inTerminal) {
					TerminalLauncher.launchInTerminal(openerString, opener.workingDir);
				}
				else {
					// TODO: we probably need to support shell patterns, etc
					// TODO: merge launchInTerminal with this code
					Runtime.getRuntime().exec(openerString, null, opener.workingDir);
				}
			}
			catch (Exception e) {
				throw new UserErrorException("opener.failed", openerString);
			}
		}
	}

	/**
	 * Replaces references to scanned values in an opener string.
	 * References look like ${fetcher_id}
	 * @param openerString
	 * @return opener string with values replaced
	 */
	String prepareOpenerStringForItem(String openerString, int selectedItem) {
		Pattern paramsPattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = paramsPattern.matcher(openerString);
		StringBuffer sb = new StringBuffer(64);
		while (matcher.find()) {
			// resolve the required fetcher
			String fetcherId = matcher.group(1);

			// retrieve the scanned value
			Object scannedValue = getScannedValue(selectedItem, fetcherId);
			if (scannedValue == null || scannedValue instanceof Empty) {
				throw new UserErrorException("opener.nullFetcherValue", fetcherId);					
			}
			
			matcher.appendReplacement(sb, scannedValue.toString());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private Object getScannedValue(int selectedItem, String fetcherId) {
		int fetcherIndex = fetcherRegistry.getSelectedFetcherIndex(fetcherId);
		if (fetcherIndex < 0) {
			throw new UserErrorException("opener.unknownFetcher", fetcherId);
		}

		Object value = scanningResults.getResult(selectedItem).getValues().get(fetcherIndex);
		
		if ((value == null || value instanceof Empty) && fetcherId.equals(HostnameFetcher.ID)) {
			// small innocent hardcode:
			// if we request a hostname, but get null, use the IP
			value = scanningResults.getResult(selectedItem).getAddress().getHostAddress();
		}
		
		return value;
	}
}
