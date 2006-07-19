/**
 * 
 */
package net.azib.ipscan.exporters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Exporter Registry singleton class.
 * Actually, it registers both plugins and builtins.
 *
 * @author anton
 */
public class ExporterRegistry {
	
	private static ExporterRegistry instance;
	
	/** All available Exporter implementations, Map of Class instances */
	private Map exporters;
	
	static {
		// TODO: maybe it is better to call it from the main class?
		initialize();
	}
	
	public static ExporterRegistry getInstance() {
		return instance;
	}
	
	public static void initialize() {
		instance = new ExporterRegistry();
	}
	
	/**
	 * Private constructor
	 */
	private ExporterRegistry() {
		exporters = new HashMap();
		Exporter exporter;
		
		exporter = new TXTExporter();
		exporters.put(exporter.getFilenameExtension(), exporter);
		exporter = new CSVExporter();
		exporters.put(exporter.getFilenameExtension(), exporter);
	}

	/**
	 * Iterates Exporter instances within this Registry
	 */
	public Iterator iterator() {
		return exporters.values().iterator();
	}
	
	/**
	 * Creates a new exporter instance examining the extension of the provided file name
	 * @param fileName the file name (with extension)
	 * @throws ExporterException in case such exporter is not registered
	 */
	public Exporter createExporter(String fileName) throws ExporterException {
		
		int extensionPos = fileName.lastIndexOf('.') + 1;
		String extension = fileName.substring(extensionPos);
		
		Exporter prototype = (Exporter) exporters.get(extension);
		if (prototype == null) {
			throw new ExporterException("exporter.unknown");
		}
		try {
			return (Exporter) prototype.clone();
		}
		catch (CloneNotSupportedException e) {
			// this is not possible
			throw new RuntimeException(e);
		}
	}
}
