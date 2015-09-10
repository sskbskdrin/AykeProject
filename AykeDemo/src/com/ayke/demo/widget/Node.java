package com.ayke.demo.widget;

import java.util.ArrayList;
import java.util.List;

import com.ayke.library.treeview.BaseTreeNode;

public class Node extends BaseTreeNode {

	private String orgName;
	public boolean isCheck;
	private List<Node> list;

	public Node(String name, int level) {
		this.orgName = name;
		setLevel(level);
		setExpand(true);
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public void setList(List<Node> list) {
		this.list = list;
	}

	@Override
	public boolean isLeaf() {
		return getChildList().size() == 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getChildList() {
		if (list == null)
			list = new ArrayList<Node>();
		return list;
	}

}
