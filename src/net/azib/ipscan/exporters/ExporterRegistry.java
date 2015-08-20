/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.exporters;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The registry of all Exporters.
 * It registers both plugins and builtins.
 *
 * @author Anton Keks
 */
@Singleton
public class ExporterRegistry implements Iterable<Exporter> {
	
	/** All available Exporter implementations, Map of Exporter instances (prototypes) */
	private Map<String, Exporter> exporters;

	@Inject public ExporterRegistry(List<Exporter> registeredExporters) {
		exporters = new LinkedHashMap<String, Exporter>();

		for (Exporter exporter : registeredExporters) {
			exporters.put(exporter.getFilenameExtension(), exporter);
		}
	}

	/**
	 * Iterates Exporter instances within this Registry
	 */
	public Iterator<Exporter> iterator() {
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
		
		Exporter prototype = exporters.get(extension);
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
