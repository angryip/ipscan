/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.util;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class NetBIOSResolverTest {
	@Test
	public void extractNamesNoUserNoGroup() throws Exception {
		var response = ("01234567890123456789012345678901234567890123456789012345\u0001" +
						   "ComputerName   XYY" + 
						   "\u00DE\u00AD\u00BE\u00EF\u0000\u0000         XYY" 
						  ).getBytes("ISO-8859-1");
		assertArrayEquals(new String[]{"ComputerName", null, null, "DE-AD-BE-EF-00-00"}, NetBIOSResolver.extractNames(response, 1));
	}
	
	@Test
	public void extractNamesNoUserWithGroup() throws Exception {
		var response = ("01234567890123456789012345678901234567890123456789012345\u0002" +
						   "ComputerName   XYY" + 
						   "GroupName      \u0000\u0080\u0000" +
						   "\u0001\u0002\u0003\u0004\u0005\u0006         XYY" 
						  ).getBytes("ISO-8859-1");
		assertArrayEquals(new String[] {"ComputerName", null, "GroupName", "01-02-03-04-05-06"}, NetBIOSResolver.extractNames(response, 2));
	}
		
	@Test
	public void extractNamesWithUserAndGroup() throws Exception {
		var response = ("01234567890123456789012345678901234567890123456789012345\u0007" +
						   "ComputerName   XYY" + 
						   "SomeName       X\u007F\u0000" +
						   "SomeName       X\u0085\u0000" +
						   "GroupName      \u0000\u0085\u0000" +
						   "WrongUserName  \u0003YY" +
						   "UserName       \u0003YY" +
						   "SomeName       XYY" +
						   "\u00DE\u00AD\u00BE\u00EF\u0000\u0000         XYY" 
						  ).getBytes("ISO-8859-1");
		assertArrayEquals(new String[] {"ComputerName", "UserName", "GroupName", "DE-AD-BE-EF-00-00"}, NetBIOSResolver.extractNames(response, 7));
	}
}
