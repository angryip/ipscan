/*
  This is a part of Angry IP Scanner source.
 */
package net.azib.ipscan.config;

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
	
	public static final String COPYLEFT = "© 2019 Anton Keks and contributors";
	
	public static final String OWN_HOST = "angryip.org";

	public static final String WEBSITE = "https://" + OWN_HOST;

	public static final String FAQ_URL = WEBSITE + "/faq/";

	public static final String PRIVACY_URL = WEBSITE + "/about/privacy.html";

	public static final String FULL_LICENSE_URL = "http://www.gnu.org/licenses/gpl-2.0.html";

	public static final String PLUGINS_URL = WEBSITE + "/contribute/plugins.html";
	
	public static final String DOWNLOAD_URL = WEBSITE + "/download/";

	public static final String ISSUES_URL = WEBSITE + "/issues/";

	public static final String IP_LOCATE_URL = WEBSITE + "/iplocate";

	public static final String LATEST_VERSION_URL = WEBSITE + "/ipscan/IPSCAN.VERSION";

	public static final String GA_ID = "UA-10776159-2";
	
	private static String version;
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
	 * @return build date of currently running Angry IP Scanner  (retrieved from the jar file)
	 */
	public static String getBuildDate() {
		if (buildDate == null) {
			loadVersionFromJar();
		}
		return buildDate;
	}

	private static void loadVersionFromJar() {
		try {
			String path = Version.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			if (path.endsWith(".jar") || path.endsWith(".exe")) {
				JarFile jarFile = new JarFile(path);
				Attributes attrs = jarFile.getManifest().getMainAttributes();
				version = attrs.getValue("Version");
				buildDate = attrs.getValue("Build-Date");
				return;
			}
		}
		catch (Exception e) {
			LoggerFactory.getLogger().log(Level.WARNING, "Cannot obtain version", e);
		}
		version = "current";
		buildDate = "today";
	}
	
	public static String getFullName() {
		return NAME + " " + getVersion();
	}
}
