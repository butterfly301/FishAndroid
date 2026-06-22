package com.teacher.game.state;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

import com.teacher.fish.GameMainActivity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Lightweight localization system.
 * Translations are loaded from UTF-8 assets/lang_{locale}.properties files.
 */
public class L10n {

	public static final String ZH = "zh";
	public static final String EN = "en";
	public static final String JA = "ja";
	public static final String FR = "fr";
	public static final String DE = "de";
	public static final String IT = "it";
	public static final String ES = "es";
	public static final String RU = "ru";

	private static final String PREF_KEY = "game_language";
	private static final String[] LOCALES = {ZH, EN, JA, FR, DE, IT, ES, RU};

	private static String sLanguage = ZH;
	private static final HashMap<String, String> sMap = new HashMap<>();
	private static boolean sLoaded = false;

	public static void init(Context context) {
		if (sLoaded) return;

		SharedPreferences prefs = context.getSharedPreferences("fish_game_prefs", Context.MODE_PRIVATE);
		sLanguage = prefs.getString(PREF_KEY, ZH);

		for (String locale : LOCALES) {
			try {
				InputStream in = GameMainActivity.assets.open("lang_" + locale + ".properties");
				InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
				Properties props = new Properties();
				props.load(reader);
				reader.close();
				in.close();
				for (String key : props.stringPropertyNames()) {
					String normalizedKey = normalizeKey(key);
					sMap.put(locale + ":" + normalizedKey, props.getProperty(key));
				}
			} catch (IOException e) {
				// File not found, skip this locale silently.
			}
		}
		sLoaded = true;
	}

	public static String getLanguage() {
		return sLanguage;
	}

	public static String getLanguageDisplayName() {
		if (ZH.equals(sLanguage)) return "\u7b80\u4f53\u4e2d\u6587";
		if (EN.equals(sLanguage)) return "English";
		if (JA.equals(sLanguage)) return "\u65e5\u672c\u8a9e";
		if (FR.equals(sLanguage)) return "Fran\u00e7ais";
		if (DE.equals(sLanguage)) return "Deutsch";
		if (IT.equals(sLanguage)) return "Italiano";
		if (ES.equals(sLanguage)) return "Espa\u00f1ol";
		if (RU.equals(sLanguage)) return "\u0420\u0443\u0441\u0441\u043a\u0438\u0439";
		return "\u7b80\u4f53\u4e2d\u6587";
	}

	public static String[] getLanguageCodes() {
		return new String[]{ZH, EN, JA, FR, DE, IT, ES, RU};
	}

	public static String[] getLanguageNames() {
		return new String[]{
				"\u7b80\u4f53\u4e2d\u6587",
				"English",
				"\u65e5\u672c\u8a9e",
				"Fran\u00e7ais",
				"Deutsch",
				"Italiano",
				"Espa\u00f1ol",
				"\u0420\u0443\u0441\u0441\u043a\u0438\u0439"
		};
	}

	public static void setLanguage(Context context, String lang) {
		sLanguage = lang;
		context.getSharedPreferences("fish_game_prefs", Context.MODE_PRIVATE)
			.edit()
			.putString(PREF_KEY, lang)
			.apply();
	}

	public static String get(String id) {
		String value = sMap.get(sLanguage + ":" + id);
		if (value == null) value = sMap.get("zh:" + id);
		return value != null ? value : "?" + id;
	}

	public static String get(String id, int arg) {
		return String.format(get(id), arg);
	}

	public static String get(String id, int arg1, int arg2) {
		return String.format(get(id), arg1, arg2);
	}

	public static String get(String id, String arg) {
		return String.format(get(id), arg);
	}

	public static String get(String id, Object... args) {
		return String.format(get(id), args);
	}

	private static String normalizeKey(String key) {
		if (key != null && key.length() > 0 && key.charAt(0) == '\ufeff') {
			return key.substring(1);
		}
		return key;
	}
}
