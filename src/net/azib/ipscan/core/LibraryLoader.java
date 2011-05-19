/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */

package net.azib.ipscan.core;

import java.io.*;

/**
 * Utility class for loading of JNI libraries from jar files.
 * 
 * @author Anton Keks
 */
public class LibraryLoader {
	
	/**
	 * Loads native library from the jar file (storing it in the temp dir)
	 * @param library JNI library name
	 */
	public static void loadLibrary(String library) {		
		String filename = System.mapLibraryName(library);
		String fullFilename = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + filename;
		try {
			// try to load from the temp dir (in case it is already there)
			System.load(fullFilename);
		}
		catch (UnsatisfiedLinkError err2) {
			try {
				// try to extract from the jar
				InputStream is = LibraryLoader.class.getClassLoader().getResourceAsStream(filename);
				if (is == null) {
					throw new IOException(filename + " not found in the jar file (classpath)");
				}
				byte[] buffer = new byte[4096];
				OutputStream os = new FileOutputStream(fullFilename);
				int read;
				while ((read = is.read(buffer)) != -1) {
					os.write(buffer, 0, read);
				}
				os.close();
				is.close();
                new File(fullFilename).setExecutable(true, false);
				System.load(fullFilename);
			}
			catch (IOException ioe) {
				throw new RuntimeException("Unable to extract native library: " + library, ioe);
			}
		}
	}

}
