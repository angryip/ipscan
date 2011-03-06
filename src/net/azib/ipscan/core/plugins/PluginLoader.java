package net.azib.ipscan.core.plugins;

import net.azib.ipscan.config.LoggerFactory;
import org.picocontainer.MutablePicoContainer;

import java.util.logging.Logger;

public class PluginLoader {
    static final Logger LOG = LoggerFactory.getLogger();

    public void addTo(MutablePicoContainer container) {
        String plugins = System.getProperty("ipscan.plugins");
        if (plugins != null) {
            String[] classes = plugins.split("\\s*,\\s*");
            for (String className : classes) {
                try {
                    Class clazz = Class.forName(className);
                    container.registerComponentImplementation(clazz);
                }
                catch (ClassNotFoundException e) {
                    LOG.warning("Unable to load plugin: " + className);
                }
            }
        }
    }
}
