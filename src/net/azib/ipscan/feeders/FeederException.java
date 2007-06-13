/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

/**
 * Exception for throwing in case of problems with Feeders.
 * 
 * @author anton
 */
public class FeederException extends IllegalArgumentException {
	
	static final long serialVersionUID = 746237846273847L;

	public FeederException(String message) {
		super(message);
	}
	
	public FeederException(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}

}
