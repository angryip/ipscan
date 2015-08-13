/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 */
package net.azib.ipscan.fetchers;

import net.azib.ipscan.config.CommentsConfig;
import net.azib.ipscan.core.ScanningSubject;

import javax.inject.Inject;

/**
 * A fetcher for displaying of user-defined comments about every IP address.
 * 
 * @author Anton Keks
 */
public class CommentFetcher extends AbstractFetcher {
	public static final String ID = "fetcher.comment";
	
	private CommentsConfig commentsConfig;
	
	@Inject public CommentFetcher(CommentsConfig commentsConfig) {
		this.commentsConfig = commentsConfig;
	}

	public String getId() {
		return ID;
	}

	public Object scan(ScanningSubject subject) {
		String mac = (String) subject.getParameter(MACFetcher.ID);
		return commentsConfig.getComment(subject.getAddress(), mac);
	}
}
