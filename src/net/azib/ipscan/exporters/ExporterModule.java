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
	public Exporter[] getDefaultExporters() {
		return new Exporter[] {new TXTExporter(), new CSVExporter(), new XMLExporter(), new IPListExporter()};
	}

//	@Provides
//	public TXTExporter getTXTExporter() {
//		return new TXTExporter();
//	}

//	@Provides
//	public Class<Exporter>[] getDefaultExporters() {
//		return new Class[] {TXTExporter.class, CSVExporter.class, XMLExporter.class, IPListExporter.class};
//	}
//
//	@Provides
//	public ExporterRegistry getExporterRegistry(Class<Exporter>[] exporters) {
//		List<Exporter> list = new ArrayList<Exporter>();
//		for (Class<Exporter> ec: exporters) {
//			try {
//				list.add(ec.newInstance());
//			}
//			catch (InstantiationException e) {
//				e.printStackTrace();
//			}
//			catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//		return new ExporterRegistry(list.toArray(new Exporter[exporters.length]));
//	}

	@Provides
	public ExporterRegistry getExporterRegistry(Exporter[] exporters) {
		return new ExporterRegistry(exporters);
	}
}
