/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import net.azib.ipscan.core.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An Exporter is a class, which is able to export scanning results into a
 * specific output format.
 * 
 * This interface is callback-like. Each method of it is called when more
 * data is available for writing.
 * 
 * The sequence of calling:
 * start, setFetchers, nextAddressResult, ..., end
 * 
 * Exporters are created by cloning (prototype pattern).
 *
 * @author Anton Keks
 */
public interface Exporter extends Cloneable, Plugin {
		
	/**
	 * @return the filename extension of the file type this Exporter produces (like txt, html, etc)
	 */
	public String getFilenameExtension();
	
	/**
	 * Tells the exporter that it should append to the specified file instead of creating a new one.
	 * @param file the file that the appending will be directed to, so that the Exporter can prepare the file before it will start appending. 
	 */
	public void shouldAppendTo(File file);
	
	/**
	 * Called on start of the exporting.
	 * @param outputStream this OutputStream should be used to output exported data.
	 * @param feederInfo summary of feeder preferences, which were used for this scan
	 * @throws IOException 
	 */
	public void start(OutputStream outputStream, String feederInfo) throws IOException;
	
	/**
	 * Called when no more data is available for exporting. This is the last
	 * method, which is called on any exporter.
	 * @throws IOException 
	 */
	public void end() throws IOException;
	
	/**
	 * Called after the start to provide the whole list of fetchers
	 * @param fetcherNames
	 * @throws IOException 
	 */
	public void setFetchers(String[] fetcherNames) throws IOException;
	
	/**
	 * Called to provide the actual scanning results for the IP address.
	 * @param results the results, returned by the Fetcher. This is an array of String 
	 * most of the time or objects, which provide toString() methods.
	 * The IP address itself is the first element in the provided array.
	 * Any element of results can be null.
	 * @throws IOException 
	 */
	public void nextAddressResults(Object[] results) throws IOException;

	/**
	 * @deprecated mispelled method for backwards-compatibility
	 */
	public void nextAdressResults(Object[] results) throws IOException;
	
	/**
	 * Clones the Exporter instance
	 */
	public Object clone() throws CloneNotSupportedException;
	
}
