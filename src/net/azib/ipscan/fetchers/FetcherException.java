/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

/**
 * FetcherException
 *
 * @author Anton Keks
 */
public class FetcherException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FetcherException(Throwable cause) {
		super(cause);
	}

	public FetcherException(String label, Throwable cause) {
		super(label, cause);
	}

	public FetcherException(String label) {
		super(label);
	}

}
