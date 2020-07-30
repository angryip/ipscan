package net.azib.ipscan.gui.fetchers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PortTextFetcherPrefsTest {
	@Test
	public void toEditableText() {
		assertEquals("Hello\\r\\nand\\x05\\x19", PortTextFetcherPrefs.toEditableText("Hello\r\nand\u0005\u0019"));
	}

	@Test
	public void toRealText() {
		assertEquals("Hello\r\nand\u0005\u0019", PortTextFetcherPrefs.toRealText("Hello\\r\\nand\\x05\\x19"));
	}
}