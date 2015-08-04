package net.azib.ipscan.exporters;

import dagger.Component;

/**
 * Created by englishman on 6/6/15.
 */
@Component(modules = ExporterModule.class)
public interface ExporterComponent {
	ExporterRegistry get();
}
