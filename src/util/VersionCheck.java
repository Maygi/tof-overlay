package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import model.MainDriver;

public class VersionCheck {
	
	private static final String URL = "https://api.github.com/repos/Maygi/ms2combatanalyzer/tags";

	private static String streamToString(InputStream inputStream) {
		Scanner s = new Scanner(inputStream, "UTF-8");
	    String text = s.useDelimiter("\\Z").next();
	    s.close();
	    return text;
	}

	private static String getJSon(String urlQueryString) {
		String json = null;
		try {
			URL url = new URL(urlQueryString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			connection.setConnectTimeout(10000);
			connection.connect();
			InputStream inStream = connection.getInputStream();
			json = streamToString(inStream); // input stream to string
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return json;
	}
	
	public static boolean needsUpdate() {
		String current = MainDriver.VERSION;
		String live = MainDriver.liveVersion;
		if (live.equals("???"))
			return false;
		return !latestVersion(current, live);
	}
	
	public static boolean latestVersion(String base, String compare) {
		String current = base;
		String live = compare;
		String[] currentParts = current.split("\\.");
		String[] liveParts = live.split("\\.");
		for (int i = 0; i < currentParts.length; i++) {
			int realCurrent = Integer.parseInt(currentParts[i]);
			if (i >= liveParts.length) { //maybe we have current 1.3.1 vs live 1.3?
				return true;
			} else {
				int realLive = Integer.parseInt(liveParts[i]);
				if (realCurrent < realLive) {
					return false;
				} else if (realCurrent > realLive) {
					return true;
				}
			}
		}
		return true;
	}
	
	/**
	 * JSON parsing with no library??
	 * @return The current version from the Git tag API.
	 */
	public static String getVersion() {
		String raw = getJSon(URL);
		if (raw == null)
			return "???";
		String latest = "1.0";
		while (raw.contains("\"name\":\"")) {
			int index = raw.indexOf("\"name\":\"");
			raw = raw.substring(index + 8, raw.length());
			String json = raw;
			index = json.indexOf("\"");
			json = json.substring(0, index);
			json = json.toUpperCase();
			json = json.replaceAll("MSCA", "");
			if (latestVersion(json, latest))
				latest = json;
			if (raw.contains("},")) {
				index = raw.indexOf("},") + 2;
				raw = raw.substring(index, raw.length());
			} else { //reached the end
				break;
			}
		}
		return latest;
	}
}
