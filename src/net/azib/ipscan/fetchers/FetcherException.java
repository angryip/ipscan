/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
  Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.core.UserErrorException;

public class FetcherException extends UserErrorException {
	public FetcherException(String label, Throwable cause) {
		super(label, cause);
	}

	public FetcherException(String label) {
		super(label);
	}

	public FetcherException(Throwable cause) {
		super(cause);
	}
}
