/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.feeders;

import net.azib.ipscan.core.UserErrorException;

/**
 * Exception for throwing in case of problems with Feeders.
 * 
 * @author Anton Keks
 */
public class FeederException extends UserErrorException {
	public FeederException(String message) {
		super(message);
	}
}
