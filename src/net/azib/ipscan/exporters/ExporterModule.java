package net.azib.ipscan.exporters;

import dagger.Module;
import dagger.Provides;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ExporterModule {
	@Provides
	public Class<Exporter>[] getDefaultExporters() {
		return new Class[] {TXTExporter.class, CSVExporter.class, XMLExporter.class, IPListExporter.class};
	}
}
