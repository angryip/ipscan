/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.gui.UserErrorException;

/**
 * OpenerLauncher
 *
 * @author anton
 */
public class OpenerLauncher {
	
	private static final Logger LOG = LoggerFactory.getLogger();
	
	private FetcherRegistry fetcherRegistry;
	private ScanningResultList scanningResults;
	
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
				LOG.log(Level.WARNING, "opener.failed", e);
				throw new UserErrorException("opener.failed", openerString);
			}
		}
	}

	/**
	 * Replaces references to scanned values in an opener string.
	 * Refefernces look like ${fetcher_label}
	 * @param openerString
	 * @return opener string with values replaced
	 */
	String prepareOpenerStringForItem(String openerString, int selectedItem) {
		Pattern paramsPattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = paramsPattern.matcher(openerString);
		StringBuffer sb = new StringBuffer(64);
		while (matcher.find()) {
			// resolve the required fetcher
			String fetcherName = matcher.group(1);
			int fetcherIndex = fetcherRegistry.getSelectedFetcherIndex(fetcherName);
			if (fetcherIndex < 0) {
				throw new UserErrorException("opener.unknownFetcher", fetcherName);
			}

			// retrieve the scanned value
			try {
				String scannedValue = getScannedValue(selectedItem, fetcherIndex);
				matcher.appendReplacement(sb, scannedValue);
			}
			catch (Exception e) {
				throw new UserErrorException("opener.nullFetcherValue", fetcherName);					
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	String getScannedValue(int selectedItem, int fetcherIndex) {
		return (String) scanningResults.getResult(selectedItem).getValues().get(fetcherIndex);
	}
}
