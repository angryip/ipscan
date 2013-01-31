package net.azib.ipscan.core.plugins;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.ScanningSubject;
import net.azib.ipscan.fetchers.AbstractFetcher;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PluginLoaderTest {
    @Test
    public void loadFromSystemProperty() {
        System.setProperty("ipscan.plugins", DummyFetcher.class.getName());
        MutablePicoContainer container = mock(MutablePicoContainer.class);
        new PluginLoader().addTo(container);
        verify(container).registerComponentImplementation(DummyFetcher.class);
    }

    public static class DummyFetcher extends AbstractFetcher {
        public Object scan(ScanningSubject subject) {
            return "dummy";
        }

        public String getId() {
            return "dummy";
        }
    }
}
