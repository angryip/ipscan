package net.azib.ipscan.core;

import net.azib.ipscan.fetchers.AbstractFetcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.picocontainer.MutablePicoContainer;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PluginLoaderTest {
	PluginLoader loader = new PluginLoader();
	MutablePicoContainer container = mock(MutablePicoContainer.class);

	@Test
    public void loadFromSystemProperty() {
        System.setProperty("ipscan.plugins", DummyFetcher.class.getName());
		loader.loadPluginsSpecifiedInSystemProperties(container);
        verify(container).registerComponentImplementation(DummyFetcher.class);
    }

	@Test
	public void canFindClassLocation() throws Exception {
		File file = loader.getClassLocation(getClass());
		assertEquals("core", file.getParentFile().getName());
		assertTrue(file.exists());
	}

	@Test
	public void loadFromJarFile() throws Exception {
		loader.loadPluginJars(container, loader.getClassLocation(getClass()));

		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		verify(container).registerComponentImplementation(classCaptor.capture());
		Class plugin = classCaptor.getValue();
		assertEquals("test.TestPlugin", plugin.getName());
		assertTrue(Plugin.class.isAssignableFrom(plugin));
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
