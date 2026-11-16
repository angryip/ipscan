/*
  This file is a part of Angry IP Scanner source code,
  see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.core.ScanningSubject;

/**
 * A fetcher for displaying of user-defined comments about every IP address.
 * 
 * @author Anton Keks
 */
public class CommentFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.comment";
	
	private CommentsConfig commentsConfig;
	private MACFetcher macFetcher;

	public CommentFetcher(CommentsConfig commentsConfig, MACFetcher macFetcher) {
		this.commentsConfig = commentsConfig;
		this.macFetcher = macFetcher;
	}

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		// comments are stored by MAC if it is known, so it is needed even if MAC is fetched later or not at all
		return commentsConfig.getComment(subject.getAddress(), macFetcher.getMAC(subject));
	}
}
