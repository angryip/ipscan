/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.fetchers;

/**
 * FetcherException
 *
 * @author anton
 */
public class FetcherException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FetcherException(Throwable cause) {
		super(cause);
	}

}
