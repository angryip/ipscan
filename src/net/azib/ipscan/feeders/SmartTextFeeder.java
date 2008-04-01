/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import java.net.InetAddress;

/**
 * Smart text feeder for advanced users.
 * 
 * TODO: implement SmartTextFeeder to accept text, e.g.
 * 127.0.0.1-255
 * 127.0-10.13-15.1
 * 127.0.0.1/24
 * 
 * Warning: IPv4-specific!
 * 
 * @author Anton Keks
 */
public class SmartTextFeeder extends AbstractFeeder {
	
	public String getId() {
		return null;
	}

	public SmartTextFeeder(String text) {
		// remove all whitespace
		text = text.replaceAll("\\w+", "");
		
		// extract netmask
		int slashPos = text.indexOf('/'); 
		if (slashPos >= 0) {
			//netmask = text.substring(slashPos+1);
			text = text.substring(0, slashPos);
		}
		
		//String[] tokens = text.split("\\.");
		// TODO: use port list parsing code here
	}
	
	public boolean hasNext() {
		return false; 
	}

	public InetAddress next() {
		return null;
	}

	public int percentageComplete() {
		return 0;
	}
	
	public String getInfo() {
		return null;
	}
}
