/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.gui.actions;

import net.azib.ipscan.config.OpenersConfig.Opener;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.core.values.Empty;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.HostnameFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class OpenerLauncher {
	
	private final FetcherRegistry fetcherRegistry;
	private final ScanningResultList scanningResults;
	
	public OpenerLauncher(FetcherRegistry fetcherRegistry, ScanningResultList scanningResults) {
		this.fetcherRegistry = fetcherRegistry;
		this.scanningResults = scanningResults;
	}

	public void launch(Opener opener, int selectedItem) {
		// check for URLs - these are opened directly, not via shell, so no sanitization needed
		boolean isURL = opener.execString.startsWith("http:") || opener.execString.startsWith("https:") ||
				opener.execString.startsWith("ftp:") || opener.execString.startsWith("mailto:") || opener.execString.startsWith("\\\\");
		var openerString = prepareOpenerStringForItem(opener.execString, selectedItem, !isURL);
		
		if (isURL) {
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
			catch (UserErrorException e) {
				throw e;
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
		var tokenizer = new StringTokenizer(command);
		List<String> result = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			var token = tokenizer.nextToken(" \t");
			
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
	 * @param openerString the opener template
	 * @param selectedItem the scanned result index
	 * @param sanitize if true, sanitize substituted values for safe shell execution
	 * @return opener string with values replaced
	 */
	String prepareOpenerStringForItem(String openerString, int selectedItem, boolean sanitize) {
		var paramsPattern = Pattern.compile("\\$\\{(.+?)\\}");
		var matcher = paramsPattern.matcher(openerString);
		var sb = new StringBuilder(64);
		while (matcher.find()) {
			// resolve the required fetcher
			var fetcherId = matcher.group(1);

			// retrieve the scanned value (never null or empty - falls back to the IP if missing)
			var scannedValue = getScannedValue(selectedItem, fetcherId);
			
			var value = scannedValue.toString();
			if (sanitize) value = Platform.WINDOWS ? sanitizeForCmd(value) : sanitizeForShell(value);
			matcher.appendReplacement(sb, value);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Sanitizes a value for safe inclusion in a POSIX shell command string (sh, bash, etc.,
	 * also used for macOS Terminal which is driven via osascript).
	 * Wraps the value in single quotes, escaping any embedded single quotes.
	 * This prevents shell metacharacters in attacker-controlled data (e.g. hostnames
	 * from reverse DNS) from being interpreted by sh or osascript.
	 */
	static String sanitizeForShell(String value) {
		if (value.isEmpty()) return "''";
		var sb = new StringBuilder(value.length() + 2);
		sb.append('\'');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '\'') {
				// end current single-quoted segment, add escaped quote, start new segment
				sb.append("'\\''");
			} else {
				sb.append(c);
			}
		}
		sb.append('\'');
		return sb.toString();
	}

	/**
	 * Sanitizes a value for safe inclusion in a Windows cmd command line
	 * (both Runtime.exec and the cmd /k terminal launcher).
	 * cmd.exe does not treat single quotes as quoting, so values are wrapped
	 * in double quotes instead, with embedded double quotes escaped cmd-style.
	 * Note: percent signs cannot be reliably neutralized in cmd, but
	 * hostnames, IPs, ports and MACs cannot contain them anyway.
	 */
	static String sanitizeForCmd(String value) {
		if (value.isEmpty()) return "\"\"";
		var sb = new StringBuilder(value.length() + 2);
		sb.append('"');
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '"') {
				// escape embedded double quotes cmd-style
				sb.append("\"\"");
			} else {
				sb.append(c);
			}
		}
		sb.append('"');
		return sb.toString();
	}

	private Object getScannedValue(int selectedItem, String fetcherId) {
		var fetcherIndex = fetcherRegistry.getSelectedFetcherIndex(fetcherId);
		if (fetcherIndex < 0) {
			if (!fetcherRegistry.isRegisteredFetcher(fetcherId)) {
				// the referenced fetcher doesn't exist at all - this is a typo in the opener string
				throw new UserErrorException("opener.unknownFetcher", fetcherId);
			}
			// the fetcher is registered but not currently selected (e.g. its column was hidden);
			// fall back to the IP address so the opener still works
			return scanningResults.getResult(selectedItem).getAddress().getHostAddress();
		}

		var value = scanningResults.getResult(selectedItem).getValues().get(fetcherIndex);
		
		if (value == null || value instanceof Empty) {
			// the value is missing (e.g. hostname could not be resolved, ports not scanned, etc.);
			// fall back to the IP address so the opener still works
			value = scanningResults.getResult(selectedItem).getAddress().getHostAddress();
		}
		
		return value;
	}
}
