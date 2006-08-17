/**
 * 
 */
package net.azib.ipscan.config;

/**
 * OpenersConfig
 *
 * @author anton
 */
public class OpenersConfig extends NamedListConfig {

	public OpenersConfig() {
		super("openers");
		
		if (size() == 0) {
			Labels labels = Labels.getInstance();
			// add default openers
			add(labels.getString("opener.web"), "http://${fetcher.ip}/");
			add(labels.getString("opener.netbios"), "\\\\${fetcher.ip}");
			add(labels.getString("opener.ftp"), "ftp://${fetcher.ip}/");
			add(labels.getString("opener.telnet"), "telnet ${fetcher.ip}");
			add(labels.getString("opener.ssh"), "ssh ${fetcher.ip}");
		}
	}
	
	
	
	

}
