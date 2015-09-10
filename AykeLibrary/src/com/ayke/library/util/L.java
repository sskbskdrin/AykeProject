package com.ayke.library.util;

import android.util.Log;

import com.ayke.library.BuildConfig;

/**
 * 日志的过滤
 *
 * @author Administrator
 *         LOG_LEVEL = 0 时,日志将不显示在控制台上
 */
public class L {
	public static int LOG_LEVEL = 6;

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;

	public static void v(String tag, String msg) {
		v(tag, msg, true);
	}

	public static void v(String tag, String msg, boolean releaseShow) {
		if (LOG_LEVEL > VERBOSE) {
			if (releaseShow || BuildConfig.DEBUG) {
				Log.v(tag, msg);
			}
		}
	}

	public static void d(String tag, String msg) {
		d(tag, msg, true);
	}

	public static void d(String tag, String msg, boolean releaseShow) {
		if (LOG_LEVEL > DEBUG) {
			if (releaseShow || BuildConfig.DEBUG) {
				Log.d(tag, msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		i(tag, msg, true);
	}

	public static void i(String tag, String msg, boolean releaseShow) {
		if (LOG_LEVEL > INFO) {
			if (releaseShow || BuildConfig.DEBUG) {
				Log.i(tag, msg);
			}
		}
	}

	public static void w(String tag, String msg) {
		w(tag, msg, true);
	}

	public static void w(String tag, String msg, boolean releaseShow) {
		if (LOG_LEVEL > WARN) {
			if (releaseShow || BuildConfig.DEBUG) {
				Log.w(tag, msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		e(tag, msg, true);
	}

	public static void e(String tag, String msg, boolean releaseShow) {
		if (LOG_LEVEL > ERROR) {
			if (releaseShow || BuildConfig.DEBUG) {
				Log.e(tag, msg);
			}
		}
	}
}
