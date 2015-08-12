package net.azib.ipscan.exporters;

import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by englishman on 6/6/15.
 */
@Module
public class ExporterModule {

	@Provides
	public Exporter[] provideExporters(TXTExporter txtExporter) {
		return new Exporter[] {txtExporter, new CSVExporter(), new XMLExporter(), new IPListExporter()};
	}
}
