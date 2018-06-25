package com.open.zxing.tesseract;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUtil {

	private static final String DICT_NAME = File.separator + "dicts" + File.separator;

	private static final Dic[] DICTS = new Dic[26];
	private static Dic mDic;
	private static Dic mDicNext;

	private static Dic read(String path, char index) {
		BufferedReader reader = null;
		try {
			InputStreamReader inputReader = new InputStreamReader(new FileInputStream(path + DICT_NAME + (char) ('A'
					+ index)));
			reader = new BufferedReader(inputReader);
			int line = 0;
			while ((line = reader.read()) > 0) {
				if (line == '[')
					start();
				else if (line == ']')
					end();
				else
					chat((char) line);
			}
			DICTS[index] = mDic;
			mDic = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		mDic = null;
		return DICTS[index];
	}

	private static void start() {
		mDic = mDicNext;
		mDicNext = null;
	}

	private static void chat(char c) {
		if (mDic == null) {
			mDic = new Dic(c);
			mDicNext = mDic;
			return;
		}
		if (c == ':') {
			mDicNext.setWord(true);
		} else
			mDicNext = mDic.addNext(c);
	}

	private static void end() {
		mDicNext = null;
		if (mDic.getParent() != null)
			mDic = mDic.getParent();
	}

	public static boolean isWord(Context context, String word) {
		String temp = word.toLowerCase(Locale.ENGLISH);
		if (TextUtils.isEmpty(temp))
			return false;
		int index = temp.charAt(0) - 'a';
		if (index < 0 || index >= 26)
			return false;
		Dic dic = DICTS[index];
		if (dic == null) {
			dic = read(context.getFilesDir().getAbsolutePath(), (char) index);
		}
		if (dic == null) {
			Log.e("ayke", "dicts uninitialized");
			return false;
		}
		return dic.isWord(temp);
	}

	public static String clipWord(String str) {
		Pattern pattern = Pattern.compile("[^A-Z^a-z|^']+");
		String[] words = pattern.split(str);
		String word = "";
		pattern = Pattern.compile("[A-Z]+[a-z]");
		for (int i = 0; i < words.length; i++) {
			Matcher matcher = pattern.matcher(words[i]);
			if (matcher.find()) {
				words[i] = words[i].replaceFirst(matcher.group().substring(0, matcher.group().length() - 2), "");
			}
			matcher = Pattern.compile("[a-z][A-Z].*").matcher(words[i]);
			if (matcher.find()) {
				words[i] = words[i].replace(matcher.group().substring(1), "");
			}
			if (words[i].length() > word.length())
				word = words[i];
		}
		return word;
	}
}
