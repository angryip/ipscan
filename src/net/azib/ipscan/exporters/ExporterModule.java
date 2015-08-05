package net.azib.ipscan.exporters;

import dagger.Module;
import dagger.Provides;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ExporterModule {

	@Provides
	public Exporter[] provideExporters() {
		return new Exporter[] {new TXTExporter(), new CSVExporter(), new XMLExporter(), new IPListExporter()};
	}

	@Provides
	public ExporterRegistry provideExporterRegistry(Exporter[] exporters) {
		return new ExporterRegistry(exporters);
	}
}
