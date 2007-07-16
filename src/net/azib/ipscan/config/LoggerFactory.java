/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.util.logging.Logger;

/**
 * LoggerFactory is an easy way to obtain Logger instances.
 *
 * @author Anton Keks Keks
 */
public class LoggerFactory {
	
	/**
	 * @return Logger instance initialized to the name of the calling class.
	 */
	public static Logger getLogger() {
	    Throwable t = new Throwable();                                             
	    StackTraceElement directCaller = t.getStackTrace()[1];                     
	    return Logger.getLogger(directCaller.getClassName());
	}

}
