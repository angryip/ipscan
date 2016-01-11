/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.io.File;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * OpenersConfig
 *
 * @author Anton Keks
 */
public class OpenersConfig extends NamedListConfig {
	
	static final Logger LOG = LoggerFactory.getLogger();

	public OpenersConfig(Preferences preferences) {
		super(preferences, "openers");
		
		if (size() == 0) {
			Labels labels = Labels.getInstance();
			// add default openers
			if (Platform.WINDOWS) add(labels.get("opener.netbios"), new Opener("\\\\${fetcher.ip}", false, null));
			add(labels.get("opener.web"), new Opener("http://${fetcher.hostname}/", false, null));			
			add(labels.get("opener.ftp"), new Opener("ftp://${fetcher.hostname}/", false, null));
			add(labels.get("opener.telnet"), new Opener("telnet ${fetcher.ip}", true, null));
			add(labels.get("opener.ping"), new Opener("ping ${fetcher.ip}", true, null));
			add(labels.get("opener.traceroute"), new Opener((Platform.WINDOWS ? "tracert" : Platform.LINUX ? "tracepath" : "traceroute") + " ${fetcher.ip}", true, null));
			if (!Platform.WINDOWS) add(labels.get("opener.ssh"), new Opener("ssh ${fetcher.ip}", true, null));
			if (!Platform.WINDOWS) add(labels.get("opener.whois"), new Opener("whois ${fetcher.ip}", true, null));
			add(labels.get("opener.geolocate"), new Opener(Version.IP_LOCATE_URL + "?ip=${fetcher.ip}", false, null));
			add(labels.get("opener.email"), new Opener("mailto:somebody@example.com?subject=${fetcher.ip} (${fetcher.hostname})", true, null));
		}
	}
	
	Object serializeValue(String value) {
		return new Opener(value);
	}
	
	public void add(String name, Object value) {
		if (value instanceof Opener)
			super.add(name, value);
		else
			// ensure only Openers are allowed here
			throw new IllegalArgumentException();
	}

	public Opener getOpener(String name) {
		return (Opener)namedList.get(name);
	}

	public static class Opener {
		public String execString;
		public boolean inTerminal;
		public File workingDir;
		
		Opener(String serialized) {
			try {
				String[] parts = serialized.split("@@@");
				execString = parts[0];
				inTerminal = parts[1].charAt(0) == '1';
				workingDir = parts.length >= 3 && parts[2].length() > 0 ? new File(parts[2]) : null;
			}
			catch (ArrayIndexOutOfBoundsException e) {
				// this happens when broken settings have been loaded
				LOG.fine("Broken opener config read: " + serialized);
			}
		}

		public Opener(String execString, boolean inTerminal, File workingDir) {
			this.execString = execString;
			this.inTerminal = inTerminal;
			this.workingDir = workingDir;
		}

		public String toString() {
			return execString + "@@@" + (inTerminal ? '1' : '0') + "@@@" + (workingDir != null ? workingDir.toString() : "");
		}
	}

}
