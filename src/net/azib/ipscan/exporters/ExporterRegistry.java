/**
 * 
 */
package net.azib.ipscan.exporters;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The registry of all Exporters.
 * It registers both plugins and builtins.
 *
 * @author anton
 */
public class ExporterRegistry {
	
	/** All available Exporter implementations, Map of Exporter instances (prototypes) */
	private Map exporters;
	
	public ExporterRegistry(Exporter[] registeredExporters) {
		exporters = new LinkedHashMap();
		
		for (int i = 0; i < registeredExporters.length; i++) {
			exporters.put(registeredExporters[i].getFilenameExtension(), registeredExporters[i]);
		}
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
