/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.values.Empty;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.HostnameFetcher;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OpenerLauncher
 *
 * @author Anton Keks
 */
public class OpenerLauncher {
	
	private final FetcherRegistry fetcherRegistry;
	private final ScanningResultList scanningResults;
	
	@Inject public OpenerLauncher(FetcherRegistry fetcherRegistry, ScanningResultList scanningResults) {
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
					if (Platform.LINUX) {
						// let shell interpret quoting and other stuff
						Runtime.getRuntime().exec(new String[] {"sh", "-c", openerString}, null, opener.workingDir);
					}
					else {
						Runtime.getRuntime().exec(splitCommand(openerString), null, opener.workingDir);
					}
				}
			}
			catch (Exception e) {
				throw new UserErrorException("opener.failed", openerString);
			}
		}
	}

	/**
	 * Splits the command provided as String into an array of parameters
	 * to be passed to the OS.
	 * This implementation supports quoting.
	 */
	static String[] splitCommand(String command) {
		StringTokenizer tokenizer = new StringTokenizer(command);
		List<String> result = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken(" \t");
			
			try {
				if (token.startsWith("\"")) {
					token = token.substring(1) + tokenizer.nextToken("\"");
					tokenizer.nextToken(" \t");
				}
				else
				if (token.startsWith("'")) {
					token = token.substring(1) + tokenizer.nextToken("'");
					tokenizer.nextToken(" \t");
				}
			}
			catch (NoSuchElementException e) {
				// probably the end of the command reached
			}
			
			result.add(token);
		}
		return result.toArray(new String[result.size()]);
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
