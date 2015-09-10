package com.ayke.demo;

public class ClassItem {

	private String className;
	private String text;
	private boolean isActivity;

	public ClassItem(String class1, String text, boolean activity) {
		setClassName(class1);
		this.setText(text);
		isActivity = activity;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}

}
