package net.azib.ipscan.util;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.config.Version;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.FINE;
import static net.azib.ipscan.config.Config.getConfig;

/**
 * Utility class to send statistics to Google Analytics (GA4).
 * https://developers.google.com/analytics/devguides/collection/protocol/ga4/sending-events?client_type=firebase
 */
public class GoogleAnalytics {
	public void report(String screen) {
		report("screen_view", screen);
	}

	public void report(String type, String content) {
		try {
			Config config = getConfig();
			if (!config.allowReports) return;
			URL url = new URL("https://www.google-analytics.com/mp/collect?measurement_id=" + Version.GA_ID + "&api_secret=" + Version.GA_SECRET);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoOutput(true);

			StringBuilder payload = new StringBuilder();
			payload.append("{");
			payload.append("\"client_id\":\"").append(config.getUUID()).append("\",");
			payload.append("\"non_personalized_ads\":true,");
			payload.append("\"events\":[{");
			payload.append("\"name\":\"").append(type).append("\",");
			payload.append("\"params\":{");
			payload.append("\"app_version\":\"").append(Version.getVersion()).append("\",");
			payload.append("\"app_name\":\"ipscan\",");
			payload.append("\"language\":\"").append(config.getLocale()).append("\",");
			payload.append("\"screen_resolution\":\"").append(config.forGUI().mainWindowSize[0]).append("x").append(config.forGUI().mainWindowSize[1]).append("\",");
			payload.append("\"os_info\":\"").append(System.getProperty("os.name")).append(" ").append(System.getProperty("os.version")).append(" ").append(System.getProperty("os.arch")).append("\",");
			payload.append("\"java_info\":\"").append("Java ").append(System.getProperty("java.version")).append("\"");

			content = content.replace("\"", "\\\"");
			if ("exception".equals(type)) {
				payload.append(",\"description\":\"").append(content).append("\"");
				payload.append(",\"fatal\":false");
			} else {
				payload.append(",\"firebase_screen\":\"").append(content).append("\"");
				payload.append(",\"firebase_screen_class\":\"").append("MainActivity").append("\"");
			}

			payload.append("}}]");
			payload.append("}");

			try (var os = conn.getOutputStream()) {
				LoggerFactory.getLogger().info(payload.toString());
				os.write(payload.toString().getBytes(UTF_8));
			}

			try (var is = conn.getInputStream()) {
				LoggerFactory.getLogger().info(new String(is.readAllBytes()));
			}

			conn.disconnect();
		}
		catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(FINE, "Failed to report", e);
		}
	}

	public void report(Throwable e) {
		report("exception", extractFirstStackFrame(e));
	}

	public void report(String message, Throwable e) {
		report("exception", message + "\n" + extractFirstStackFrame(e));
	}

	static String extractFirstStackFrame(Throwable e) {
		if (e == null) return "";
		StackTraceElement[] stackTrace = e.getStackTrace();
		StackTraceElement element = null;
		for (StackTraceElement stackTraceElement : stackTrace) {
			element = stackTraceElement;
			if (element.getClassName().startsWith("net.azib.ipscan")) break;
		}
		int code = e instanceof SWTError ? ((SWTError) e).code : e instanceof SWTException ? ((SWTException) e).code : -1;
		return e + (code >= 0 ? " (" + code + ")" : "") + (element == null ? "" : "\n" +
			   element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber()) +
			   (e.getCause() != null ? ";\n" + extractFirstStackFrame(e.getCause()) : "");
	}

	public void asyncReport(final String screen) {
		new Thread(() -> report(screen)).start();
	}

	public static void main(String[] args) {
		new GoogleAnalytics().report("hello");
	}
}
