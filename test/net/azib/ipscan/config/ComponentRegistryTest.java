package net.azib.ipscan.config;

import net.azib.ipscan.di.Injector;
import net.azib.ipscan.gui.MacApplicationMenu;
import net.azib.ipscan.gui.MainWindow;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ComponentRegistryTest {
	@Test
	public void mainClassesCanBeCreated() {
		Injector injector = new ComponentRegistry().init();
		assertNotNull(injector.require(CommandLineProcessor.class));
		assertNotNull(injector.require(MainWindow.class));
		assertNotNull(injector.require(MacApplicationMenu.class));
	}
}