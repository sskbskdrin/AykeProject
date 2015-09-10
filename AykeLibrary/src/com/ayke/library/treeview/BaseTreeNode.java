package com.ayke.library.treeview;

import java.util.List;

public abstract class BaseTreeNode {

	private boolean isExpand;
	private boolean isLeaf;
	private int mLevel;

	public final boolean isExpand() {
		return isExpand;
	}

	public final void setExpand(boolean expand) {
		isExpand = expand;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public final void setLeaf(boolean leaf) {
		isLeaf = leaf;
	}

	public final int getLevel() {
		return mLevel;
	}

	public final void setLevel(int level) {
		mLevel = level;
	}

	public abstract <T extends BaseTreeNode> List<T> getChildList();

}
