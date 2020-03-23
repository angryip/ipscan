package net.azib.ipscan.core;

import net.azib.ipscan.fetchers.AbstractFetcher;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PluginLoaderTest {
	PluginLoader loader = new PluginLoader();
	List<Class<? extends Plugin>> container = new ArrayList<>();

	@Test
    public void loadFromSystemProperty() {
        System.setProperty("ipscan.plugins", DummyFetcher.class.getName());
		loader.loadPluginsSpecifiedInSystemProperties(container);
        assertEquals(DummyFetcher.class, container.get(0));
    }

	@Test
	public void canFindClassLocation() {
		File file = loader.getClassLocation(getClass());
		assertEquals("core", file.getParentFile().getName());
		assertTrue(file.exists());
		assertTrue(new File(file.getParent(), getClass().getSimpleName() + ".class").exists());
	}

	@Test
	public void loadFromJarFile() {
		File pluginLocation = loader.getResourceLocation(getClass().getResource("test-plugin.jar"));
		loader.loadPluginJars(container, new File(pluginLocation.getParentFile(), "ipscan.jar"));

		Class<?> plugin = container.get(0);
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
