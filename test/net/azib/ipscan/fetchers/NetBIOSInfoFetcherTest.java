/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.fetchers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * NetBIOSInfoFetcherTest
 *
 * @author Anton Keks
 */
public class NetBIOSInfoFetcherTest {
	@Test
	public void extractNamesNoUserNoGroup() throws Exception {
		byte[] response = ("01234567890123456789012345678901234567890123456789012345\u0001" +
						   "ComputerName   XYY" + 
						   "\u00DE\u00AD\u00BE\u00EF\u0000\u0000         XYY" 
						  ).getBytes("ISO-8859-1");
		assertEquals("ComputerName [DE-AD-BE-EF-00-00]", NetBIOSInfoFetcher.extractNames(response, 1));
	}
	
	@Test
	public void extractNamesNoUserWithGroup() throws Exception {
		byte[] response = ("01234567890123456789012345678901234567890123456789012345\u0002" +
						   "ComputerName   XYY" + 
						   "GroupName      \u0000\u0080\u0000" +
						   "\u0001\u0002\u0003\u0004\u0005\u0006         XYY" 
						  ).getBytes("ISO-8859-1");
		assertEquals("GroupName\\ComputerName [01-02-03-04-05-06]", NetBIOSInfoFetcher.extractNames(response, 2));
	}
		
	@Test
	public void extractNamesWithUserAndGroup() throws Exception {
		byte[] response = ("01234567890123456789012345678901234567890123456789012345\u0007" +
						   "ComputerName   XYY" + 
						   "SomeName       X\u007F\u0000" +
						   "SomeName       X\u0085\u0000" +
						   "GroupName      \u0000\u0085\u0000" +
						   "WrongUserName  \u0003YY" +
						   "UserName       \u0003YY" +
						   "SomeName       XYY" +
						   "\u00DE\u00AD\u00BE\u00EF\u0000\u0000         XYY" 
						  ).getBytes("ISO-8859-1");
		assertEquals("GroupName\\UserName@ComputerName [DE-AD-BE-EF-00-00]", NetBIOSInfoFetcher.extractNames(response, 7));
	}
	
}
