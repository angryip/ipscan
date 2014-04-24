/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.core;

import net.azib.ipscan.config.Labels;

/**
 * Exception for throwing in case of user errors.
 * These generally result in showing an error message.
 * 
 * @author Anton Keks
 */
public class UserErrorException extends RuntimeException {
	
	private static final long serialVersionUID = 123283472834982L;
	
	public UserErrorException(String label) {
		super(Labels.getLabel("exception.UserErrorException." + label));
	}
	
	public UserErrorException(String label, Throwable cause) {
		this(label);
		initCause(cause);
	}
	
	public UserErrorException(String label, String rawInfo) {
		super(Labels.getLabel("exception.UserErrorException." + label) + rawInfo);
	}

}
