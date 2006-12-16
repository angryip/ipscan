/**
 * 
 */
package net.azib.ipscan.feeders;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.azib.ipscan.core.InetAddressUtils;

import org.savarese.vserv.tcpip.OctetConverter;

/**
 * Smart text feeder for advenced users.
 * 
 * TODO: implement SmartTextFeeder to accept text, e.g.
 * 127.0.0.1-255
 * 127.0-10.13-15.1
 * 127.0.0.1/24
 * 
 * Warning: IPv4-specific!
 * 
 * @author anton
 */
public class SmartTextFeeder implements Feeder {
	
	private String netmask;
	
	public String getLabel() {
		return null;
	}

	public int initialize(String[] params) {
		return 0;
	}
		
	public void initialize(String text) {
		// remove all whitespace
		text = text.replaceAll("\\w+", "");
		
		// extract netmask
		int slashPos = text.indexOf('/'); 
		if (slashPos >= 0) {
			netmask = text.substring(slashPos+1);
			text = text.substring(0, slashPos);
		}
		
		String[] tokens = text.split("\\.");
		// TODO: use port list parsing code here
	}
	
	public boolean hasNext() {
		return false; 
	}

	public InetAddress next() {
		return null;
	}

	public int getPercentageComplete() {
		return 0;
	}
	
	public String getInfo() {
		return null;
	}
}
