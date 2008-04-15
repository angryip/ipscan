/**
 * This is a part of Angry IP Scanner source.
 */
package net.azib.ipscan.config;

import java.net.URI;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Class with accessors to version information of the program.
 *
 * @author Anton Keks
 */
public class Version {
	public static final String NAME = "Angry IP Scanner";
	
	public static final String COPYLEFT = "© 2008 Anton Keks";
	
	public static final String WEBSITE = "http://www.azib.net/ipscan/";
	
	public static final String MAILTO = "support@azib.net";

	public static final String FORUM_URL = "http://www.azib.net/w/Forum";
	
	public static final String FAQ_URL = "http://www.azib.net/w/FAQ";
	
	public static final String FAQ_CRIPPLED_WINDOWS_URL = "http://www.azib.net/w/FAQ:_Crippled_Windows";

	public static final String PLUGINS_URL = "http://www.azib.net/w/FAQ:_Plugins";
	
	public static final String DOWNLOAD_URL = "http://www.azib.net/w/Download";

	public static final String LATEST_VERSION_URL = "http://www.azib.net/ipscan/IPSCAN.VERSION";
	
	private static String version;
	private static String build;
	private static String buildDate;
	
	/**
	 * @return version of currently running Angry IP Scanner (retrieved from the jar file)
	 */
	public static String getVersion() {
		if (version == null) {
			loadVersionFromJar();
		}
		return version;
	}
	
	/**
	 * @return build number of currently running Angry IP Scanner (retrieved from the jar file)
	 */
	public static String getBuildNumber() {
		if (build == null) {
			loadVersionFromJar();
		}
		return build;
	}

	/**
	 * @return build date of currently running Angry IP Scanner  (retrieved from the jar file)
	 */
	public static String getBuildDate() {
		if (buildDate == null) {
			loadVersionFromJar();
		}
		return buildDate;
	}

	private static void loadVersionFromJar() {
		String path = Version.class.getClassLoader().getResource(Version.class.getName().replace('.', '/') + ".class").toString();
		if (path.startsWith("jar:file:")) {
			path = path.substring(4, path.indexOf('!'));
			try {
				JarFile jarFile = new JarFile(new URI(path).getPath());
				Attributes attrs = jarFile.getManifest().getMainAttributes();
				version = attrs.getValue("Version");
				build = attrs.getValue("Build");
				buildDate = attrs.getValue("Build-Date");
				return;
			}
			catch (Exception e) {
				LoggerFactory.getLogger().log(Level.WARNING, "Cannot obtain version", e);
			}
		}
		version = "current";
		build = "unknown";
		buildDate = "today";
	}
	
	public static String getFullName() {
		return NAME + " " + getVersion();
	}
}
