package com.ayke.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author kay SharePreference工具类
 */
public class SharePreferenceUtil {

	private static final String CONFIG_NAME = "default_config";
	private static SharedPreferences mSheSharedPreferences;

	public static void init(Context context) {
		init(context, CONFIG_NAME);
	}

	public static void init(Context context, String name) {
		init(context, name, Context.MODE_PRIVATE);
	}

	public static void init(Context context, String name, int mode) {
		mSheSharedPreferences = context.getSharedPreferences(name, mode);
	}

	public static boolean isInit() {
		return mSheSharedPreferences != null;
	}

	public static boolean isExists(String key) {
		return isInit() && mSheSharedPreferences.contains(key);
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void saveInt(String key, int value) {
		if (isInit()) {
			SharedPreferences.Editor editor = mSheSharedPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(String key, int defaultValue) {
		if (isInit()) {
			return mSheSharedPreferences.getInt(key, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * @param key
	 * @return 默认0
	 */
	public static int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(String key, boolean value) {
		if (isInit()) {
			SharedPreferences.Editor editor = mSheSharedPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}

	/**
	 * @param key
	 * @return 默认false
	 */
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		if (isInit()) {
			return mSheSharedPreferences.getBoolean(key, false);
		}
		return defaultValue;
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void saveString(String key, String value) {
		if (isInit()) {
			SharedPreferences.Editor editor = mSheSharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * @param key
	 * @return默认null
	 */
	public static String getString(String key) {
		return getString(key, "");
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getString(String key,
	                               String defaultValue) {
		if (isInit()) {
			return mSheSharedPreferences.getString(key, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void saveFloat(String key, float value) {
		if (isInit()) {
			SharedPreferences.Editor editor = mSheSharedPreferences.edit();
			editor.putFloat(key, value);
			editor.commit();
		}
	}

	/**
	 * @param key
	 * @return 默认值0
	 */
	public static float getFloat(String key) {
		if (isInit()) {
			return mSheSharedPreferences.getFloat(key, 0);
		}
		return 0;
	}

	/**
	 * @param key
	 * @param value
	 */
	public static void saveLong(String key, long value) {
		if (isInit()) {
			SharedPreferences.Editor editor = mSheSharedPreferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	/**
	 * @param key
	 * @return 默认0
	 */
	public static long getLong(String key) {
		if (isInit()) {
			return mSheSharedPreferences.getLong(key, 0);
		}
		return 0;
	}

	public static void clear() {
		if (isInit()) {
			mSheSharedPreferences.edit().clear().commit();
		}
	}
}
