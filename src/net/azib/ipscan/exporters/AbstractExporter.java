/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.azib.net/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.exporters;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import net.azib.ipscan.config.Labels;

/**
 * Helper base class of all built-in exporters
 *
 * @author Anton Keks
 */
public abstract class AbstractExporter implements Exporter {
	
	protected PrintWriter output;
	protected boolean append;

	public String getName() {
		return Labels.getLabel(getId());
	}
	
	public void shouldAppendTo(File file) {
		this.append = true;
	}

	public void start(OutputStream outputStream, String feederInfo) throws IOException {
		output = new PrintWriter(new OutputStreamWriter(outputStream));
	}

	public void end() throws IOException {
		// this does the flush internally as well
		if (output.checkError())
			throw new IOException();
	}

	public Exporter clone() {
		try {
			return (Exporter) super.clone();
		}
		catch (CloneNotSupportedException e) {
			// this cannot happen
			return null;
		}
	}
}
