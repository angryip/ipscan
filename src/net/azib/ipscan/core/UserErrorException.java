/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
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
	public UserErrorException(String label) {
		super(label);
	}
	
	public UserErrorException(String label, Throwable cause) {
		super(label, cause);
	}

	public UserErrorException(Throwable cause) {
		super(cause);
	}

	public UserErrorException(String label, String rawInfo) {
		super(Labels.getLabel("exception.UserErrorException." + label) + rawInfo);
	}
}
