package net.azib.ipscan.util;

import net.azib.ipscan.config.Config;
import net.azib.ipscan.config.Version;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

public class GoogleAnalytics {
	public void report(String screen) {
		try {
			URL url = new URL("https://www.google-analytics.com/collect");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			String payload = "v=1&t=screenview&tid=" + Version.GA_ID + "&cid=" + UUID.randomUUID() + "&an=ipscan&cd=" + URLEncoder.encode(screen, "UTF-8") + "&ul=" + Config.getConfig().getLocale();
			os.write(payload.getBytes());
			os.close();
			conn.getContent();
			conn.disconnect();
		}
		catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(WARNING, "Failed to report", e);
		}
	}

	public void asyncReport(final String screen) {
		new Thread() {
			@Override public void run() {
				report(screen);
			}
		}.start();
	}

}
