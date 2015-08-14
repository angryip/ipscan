/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import java.util.prefs.Preferences;

import net.azib.ipscan.feeders.FeederCreator;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * FavoritesConfig
 *
 * @author Anton Keks
 */
@Singleton
public class FavoritesConfig extends NamedListConfig {

	@Inject
	public FavoritesConfig(Preferences preferences) {
		super(preferences, "favorites");
	}

	public void add(String key, FeederCreator feederCreator) {
		StringBuilder serializedFeeder = new StringBuilder(feederCreator.getFeederId());
		serializedFeeder.append('\t');
		for (String part : feederCreator.serialize()) {
			serializedFeeder.append(part).append(":::");
		}
		super.add(key, serializedFeeder.toString());
	}
	
	public String getFeederId(String key) {
		String value = get(key);
		int indexOf = value.indexOf('\t');
		return value.substring(0, indexOf);
	}
	
	public String[] getSerializedParts(String key) {
		String value = get(key);
		int indexOf = value.indexOf('\t');
		return value.substring(indexOf+1).split(":::");		
	}
}
