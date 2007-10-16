/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.core.ScanningSubject;

/**
 * A fetcher for displaying of user-defined comments about every IP address.
 * 
 * TODO: implement CommentFetcher
 * TODO: make an editor for comments
 *
 * @author Anton Keks
 */
public class CommentFetcher implements Fetcher {
	
	public static final String LABEL = "fetcher.comment";
	
	private CommentsConfig commentsConfig;
	
	public CommentFetcher(CommentsConfig commentsConfig) {
		this.commentsConfig = commentsConfig;
	}

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#getLabel()
	 */
	public String getLabel() {
		return LABEL;
	}

	/**
	 * @see net.azib.ipscan.fetchers.Fetcher#scan(net.azib.ipscan.core.ScanningSubject)
	 */
	public Object scan(ScanningSubject subject) {
		return commentsConfig.getComment(subject.getAddress());
	}

	public void init() {
	}

	public void cleanup() {
	}

}
