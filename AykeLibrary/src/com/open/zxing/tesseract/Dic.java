package com.open.zxing.tesseract;

import java.util.ArrayList;
import java.util.List;

public class Dic {
	private char mAlpha = 0;
	private List<Dic> list = new ArrayList<Dic>();
	private boolean isWord = false;
	private Dic parent;

	public Dic(char c) {
		mAlpha = c;
	}

	public Dic addNext(char c) {
		Dic dic = new Dic(c);
		dic.parent = this;
		list.add(dic);
		return dic;
	}

	public void setWord(boolean word) {
		isWord = word;
	}

	public Dic getParent() {
		return parent;
	}

	public Dic contain(char c) {
		for (Dic d : list) {
			if (d.mAlpha == c)
				return d;
		}
		return null;
	}

	public boolean isWord(String word) {
		if (word == null || word.length() <= 0)
			return false;
		if (isWord && word.length() == 1 && word.charAt(0) == mAlpha)
			return true;
		if (word.charAt(0) == mAlpha) {
			for (Dic d : list) {
				if (d.isWord(word.substring(1)))
					return true;
			}
		}
		return false;
	}

	public void show(StringBuilder sb) {
		sb.append(mAlpha);
		if (isWord)
			System.out.println(":" + sb.toString());
		for (Dic d : list)
			d.show(new StringBuilder(sb));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("" + mAlpha);
		if (isWord)
			builder.append(":");
		if (list.size() > 0) {
			builder.append("[");
			for (Dic d : list) {
				builder.append(d.toString());
			}
			builder.append("]");
		}
		return builder.toString();
	}
}
